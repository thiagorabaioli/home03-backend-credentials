package home03.credenciais.services;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.entities.*;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.*;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class RegistoService {

    private static final List<EstadoCredencial> ESTADOS_ATIVOS = List.of(
            EstadoCredencial.PENDENTE,
            EstadoCredencial.AGUARDA_VALIDACAO_ES,
            EstadoCredencial.AGUARDA_APROVACAO_RM,
            EstadoCredencial.APROVADA,
            EstadoCredencial.ENTRADA_AUTORIZADA
    );

    private final CredencialRepository credencialRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final EmpresaRepository empresaRepository;
    private final DocumentoService documentoService;
    private final AuditService auditService;
    private final EmailService emailService;
    private final CodigoService codigoService;

    public RegistoService(CredencialRepository credencialRepository,
                          ColaboradorRepository colaboradorRepository,
                          EmpresaRepository empresaRepository,
                          DocumentoService documentoService,
                          AuditService auditService,
                          EmailService emailService,
                          CodigoService codigoService) {
        this.credencialRepository = credencialRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.empresaRepository = empresaRepository;
        this.documentoService = documentoService;
        this.auditService = auditService;
        this.emailService = emailService;
        this.codigoService = codigoService;
    }

    @Transactional
    public Credencial registar(RegistoPublicoDTO dto, List<MultipartFile> documentos) {

        // Procurar empresa pelo nome (texto livre) — se não encontrar, criar com placeholder
        Empresa empresa = empresaRepository.findByNome(dto.getEmpresaNome())
                .orElseGet(() -> {
                    Empresa novaEmpresa = new Empresa();
                    novaEmpresa.setNome(dto.getEmpresaNome());
                    novaEmpresa.setNif("PENDENTE");
                    return empresaRepository.save(novaEmpresa);
                });

        // Reutilizar CE por código interno (se fornecido) ou criar novo
        boolean ceNovo = false;
        ColaboradorExterno colaborador;
        if (StringUtils.hasText(dto.getCodigoInterno())) {
            colaborador = colaboradorRepository.findByCodigoInterno(dto.getCodigoInterno())
                    .orElseThrow(() -> new BusinessException(
                            "Código de colaborador não encontrado: " + dto.getCodigoInterno()));
        } else {
            colaborador = criarColaborador(dto);
            ceNovo = true;
        }

        // Verificar se já existe credencial ativa para (CE + empresa + tipoColaborador)
        boolean temCredencialAtiva = credencialRepository
                .existsByColaboradorCodigoInternoAndEmpresaNomeAndTipoColaboradorAndEstadoIn(
                        colaborador.getCodigoInterno(), dto.getEmpresaNome(),
                        dto.getTipoColaborador(), ESTADOS_ATIVOS);
        if (temCredencialAtiva) {
            throw new BusinessException(
                    "Já existe uma credencial ativa para este colaborador, empresa e tipo de função.");
        }

        // Criar a credencial com código CD-YYYY-NNNN
        Credencial credencial = new Credencial();
        credencial.setCodigoInterno(codigoService.gerarCodigoCD());
        credencial.setColaborador(colaborador);
        credencial.setEmpresa(empresa);
        credencial.setEmpresaNome(dto.getEmpresaNome());
        credencial.setTipoColaborador(dto.getTipoColaborador());
        credencial.setDataInicio(dto.getDataInicio());
        credencial.setDataFim(dto.getDataFim());
        credencial.setEstado(EstadoCredencial.PENDENTE);
        credencialRepository.save(credencial);

        // Seguro
        Seguro seguro = new Seguro();
        seguro.setCredencial(credencial);
        seguro.setApolice(dto.getApolice());
        seguro.setSeguradora(dto.getSeguradora());
        seguro.setDataInicio(dto.getSeguroDataInicio());
        seguro.setDataFim(dto.getSeguroDataFim());
        credencial.setSeguro(seguro);

        // Ficha de Aptidão Médica (opcional — CE tem 15 dias para apresentar)
        if (dto.getFichaDataEmissao() != null || dto.getFichaDataValidade() != null
                || dto.getFichaResultado() != null) {
            FichaAptidaoMedica ficha = new FichaAptidaoMedica();
            ficha.setCredencial(credencial);
            ficha.setDataEmissao(dto.getFichaDataEmissao());
            ficha.setDataValidade(dto.getFichaDataValidade());
            ficha.setResultado(dto.getFichaResultado());
            credencial.setFichaAptidaoMedica(ficha);
        }

        // Horário de Trabalho
        HorarioTrabalho horario = new HorarioTrabalho();
        horario.setCredencial(credencial);
        horario.setDiasSemana(dto.getDiasSemana());
        horario.setHoraEntrada(dto.getHoraEntrada());
        horario.setHoraSaida(dto.getHoraSaida());
        credencial.setHorarioTrabalho(horario);

        // Documentos genéricos (1 a 3 ficheiros)
        if (documentos != null) {
            for (MultipartFile ficheiro : documentos) {
                if (ficheiro != null && !ficheiro.isEmpty()) {
                    Documento doc = documentoService.guardar(ficheiro, credencial.getId());
                    doc.setCredencial(credencial);
                    credencial.getDocumentos().add(doc);
                }
            }
        }

        Credencial saved = credencialRepository.save(credencial);

        // Transição para AGUARDA_VALIDACAO_ES
        saved.setEstado(EstadoCredencial.AGUARDA_VALIDACAO_ES);
        credencialRepository.save(saved);
        auditService.registarTransicao(saved, EstadoCredencial.PENDENTE,
                EstadoCredencial.AGUARDA_VALIDACAO_ES, "SISTEMA", "Registo público submetido");

        // Notificações
        if (ceNovo) {
            emailService.enviarCodigoColaborador(saved);
        } else {
            emailService.enviarConfirmacaoColaborador(saved);
        }

        return saved;
    }

    private ColaboradorExterno criarColaborador(RegistoPublicoDTO dto) {
        ColaboradorExterno ce = new ColaboradorExterno();
        ce.setCodigoInterno(codigoService.gerarCodigoCE());
        ce.setNome(dto.getNome());
        ce.setEmail(dto.getEmail());
        ce.setDataNascimento(dto.getDataNascimento());
        return colaboradorRepository.save(ce);
    }
}
