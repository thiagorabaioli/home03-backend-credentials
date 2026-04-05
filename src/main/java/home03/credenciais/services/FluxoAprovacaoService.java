package home03.credenciais.services;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.TokenAprovacao;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FluxoAprovacaoService {

    private final CredencialRepository credencialRepository;
    private final TokenService tokenService;
    private final AuditService auditService;
    private final EmailService emailService;

    public FluxoAprovacaoService(CredencialRepository credencialRepository,
                                 TokenService tokenService,
                                 AuditService auditService,
                                 EmailService emailService) {
        this.credencialRepository = credencialRepository;
        this.tokenService = tokenService;
        this.auditService = auditService;
        this.emailService = emailService;
    }

    // ES submete credencial para validação após registo público
    @Transactional
    public Credencial submeterParaValidacaoES(UUID credencialId) {
        Credencial credencial = buscarCredencial(credencialId);
        validarTransicao(credencial, EstadoCredencial.PENDENTE, EstadoCredencial.AGUARDA_VALIDACAO_ES);

        EstadoCredencial anterior = credencial.getEstado();
        credencial.setEstado(EstadoCredencial.AGUARDA_VALIDACAO_ES);
        credencialRepository.save(credencial);
        auditService.registarTransicao(credencial, anterior, EstadoCredencial.AGUARDA_VALIDACAO_ES, "SISTEMA", null);

        return credencial;
    }

    // ES rejeita por dados inválidos — CE tem de reiniciar o processo
    @Transactional
    public Credencial rejeitarPorDadosInvalidos(UUID credencialId, String responsavelES, String motivo) {
        Credencial credencial = buscarCredencial(credencialId);
        validarTransicao(credencial, EstadoCredencial.AGUARDA_VALIDACAO_ES, EstadoCredencial.REJEITADA_ES);

        EstadoCredencial anterior = credencial.getEstado();
        credencial.setEstado(EstadoCredencial.REJEITADA_ES);
        credencialRepository.save(credencial);
        auditService.registarTransicao(credencial, anterior, EstadoCredencial.REJEITADA_ES, responsavelES, motivo);
        emailService.enviarRejeicaoDadosInvalidosCE(credencial, motivo);

        return credencial;
    }

    // ES envia link ao RM para aprovação final
    @Transactional
    public TokenAprovacao enviarParaAprovacaoRM(UUID credencialId, String responsavelES) {
        Credencial credencial = buscarCredencial(credencialId);
        validarTransicao(credencial, EstadoCredencial.AGUARDA_VALIDACAO_ES, EstadoCredencial.AGUARDA_APROVACAO_RM);

        if (credencial.getSeguro() == null || !credencial.getSeguro().isValido()) {
            throw new BusinessException("Seguro inválido ou expirado — não é possível avançar para aprovação.");
        }
        // FAM é opcional no registo (CE tem 15 dias para apresentar)
        // Só bloqueia se foi submetida e está inválida (NAO_APTO ou expirada)
        if (credencial.getFichaAptidaoMedica() != null
                && credencial.getFichaAptidaoMedica().getResultado() != null
                && !credencial.getFichaAptidaoMedica().isValida()) {
            throw new BusinessException("Ficha de aptidão médica inválida ou expirada — não é possível avançar para aprovação.");
        }

        // Empresa tem de ter responsável de mercado para receber o link
        if (credencial.getEmpresa() == null
                || credencial.getEmpresa().getResponsavel() == null
                || credencial.getEmpresa().getResponsavel().getEmail() == null) {
            throw new BusinessException(
                    "A empresa não tem responsável de mercado associado. "
                    + "Atribua um responsável à empresa antes de enviar para aprovação.");
        }

        EstadoCredencial anterior = credencial.getEstado();
        credencial.setEstado(EstadoCredencial.AGUARDA_APROVACAO_RM);
        credencialRepository.save(credencial);

        TokenAprovacao token = tokenService.gerarTokenRM(credencial);
        auditService.registarTransicao(credencial, anterior, EstadoCredencial.AGUARDA_APROVACAO_RM, responsavelES, "Link enviado ao RM");
        emailService.enviarLinkAprovacaoRM(credencial, token);

        return token;
    }

    // RM aprova a credencial via link dinâmico
    @Transactional
    public Credencial aprovarPeloRM(UUID tokenId) {
        TokenAprovacao token = tokenService.validarEConsumirToken(tokenId);
        Credencial credencial = token.getCredencial();
        validarTransicao(credencial, EstadoCredencial.AGUARDA_APROVACAO_RM, EstadoCredencial.APROVADA);

        EstadoCredencial anterior = credencial.getEstado();
        credencial.setEstado(EstadoCredencial.APROVADA);
        credencialRepository.save(credencial);
        auditService.registarTransicao(credencial, anterior, EstadoCredencial.APROVADA, "RM", null);
        emailService.enviarAprovacaoES(credencial);

        return credencial;
    }

    // RM rejeita a credencial via link dinâmico
    @Transactional
    public Credencial rejeitarPeloRM(UUID tokenId, String motivo) {
        TokenAprovacao token = tokenService.validarEConsumirToken(tokenId);
        Credencial credencial = token.getCredencial();
        validarTransicao(credencial, EstadoCredencial.AGUARDA_APROVACAO_RM, EstadoCredencial.REJEITADA_RM);

        EstadoCredencial anterior = credencial.getEstado();
        credencial.setEstado(EstadoCredencial.REJEITADA_RM);
        credencialRepository.save(credencial);
        auditService.registarTransicao(credencial, anterior, EstadoCredencial.REJEITADA_RM, "RM", motivo);
        emailService.enviarRejeicaoRMParaES(credencial, motivo);

        return credencial;
    }

    // ES autoriza entrada após aprovação do RM
    @Transactional
    public Credencial autorizarEntrada(UUID credencialId, String responsavelES) {
        Credencial credencial = buscarCredencial(credencialId);
        validarTransicao(credencial, EstadoCredencial.APROVADA, EstadoCredencial.ENTRADA_AUTORIZADA);

        EstadoCredencial anterior = credencial.getEstado();
        credencial.setEstado(EstadoCredencial.ENTRADA_AUTORIZADA);
        credencialRepository.save(credencial);
        auditService.registarTransicao(credencial, anterior, EstadoCredencial.ENTRADA_AUTORIZADA, responsavelES, null);

        return credencial;
    }

    private Credencial buscarCredencial(UUID id) {
        return credencialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada: " + id));
    }

    private void validarTransicao(Credencial credencial, EstadoCredencial esperado, EstadoCredencial destino) {
        if (credencial.getEstado() != esperado) {
            throw new BusinessException(
                    "Transição inválida: credencial está em " + credencial.getEstado()
                            + " mas é necessário " + esperado + " para ir para " + destino);
        }
    }
}
