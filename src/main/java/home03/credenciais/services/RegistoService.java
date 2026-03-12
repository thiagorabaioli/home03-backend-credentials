package home03.credenciais.services;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.Documento;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class RegistoService {

    private final CredencialRepository credencialRepository;
    private final EmailService emailService;

    @Value("${security.notification-emails}")
    private String securityEmails;

    public RegistoService(CredencialRepository credencialRepository, EmailService emailService) {
        this.credencialRepository = credencialRepository;
        this.emailService = emailService;
    }

    /**
     * Processa o pré-registo público de um colaborador externo.
     * Fluxo: guarda credencial → envia email ao colaborador → envia email ao responsável
     */
    @Transactional
    public Credencial processarPreRegisto(RegistoPublicoDTO dto, List<MultipartFile> ficheiros) {
        if (ficheiros != null && ficheiros.size() > 3) {
            throw new BusinessException("Máximo de 3 documentos permitido");
        }

        Credencial credencial = new Credencial();
        credencial.setNome(dto.getNome());
        credencial.setEmail(dto.getEmail());
        credencial.setDataNascimento(dto.getDataNascimento());
        credencial.setEmpresa(dto.getEmpresa());
        credencial.setTipo(dto.getTipo());
        credencial.setEmailResponsavel(dto.getEmailResponsavel());
        credencial.setDataValidadeCredencial(dto.getDataValidadeCredencial());
        credencial.setDataValidadeFichaAptidao(dto.getDataValidadeFichaAptidao());
        credencial.setNumApolice(dto.getNumApolice());
        credencial.setDataValidadeSeguro(dto.getDataValidadeSeguro());
        credencial.setEstado(EstadoCredencial.PENDENTE);

        // Anexar documentos
        if (ficheiros != null) {
            for (MultipartFile file : ficheiros) {
                if (!file.isEmpty()) {
                    try {
                        Documento doc = new Documento(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                        );
                        credencial.addDocumento(doc);
                    } catch (IOException e) {
                        throw new BusinessException("Erro ao processar o ficheiro: " + file.getOriginalFilename());
                    }
                }
            }
        }

        Credencial saved = credencialRepository.save(credencial);

        // Actualiza estado e envia emails
        saved.setEstado(EstadoCredencial.AGUARDA_VALIDACAO);
        credencialRepository.save(saved);

        emailService.enviarConfirmacaoColaborador(saved);
        emailService.enviarPedidoValidacaoResponsavel(saved);

        return saved;
    }

    /**
     * Processa a resposta do responsável (link do email).
     * Acao aceite: "APROVAR" ou "REJEITAR"
     */
    @Transactional
    public String processarValidacao(String token, String acao) {
        Credencial credencial = credencialRepository.findByTokenValidacao(token)
            .orElseThrow(() -> new ResourceNotFoundException("Token inválido ou expirado"));

        if (credencial.getEstado() != EstadoCredencial.AGUARDA_VALIDACAO) {
            return paginaHtmlResultado("Credencial já processada",
                "Esta credencial já foi " + credencial.getEstado().name().toLowerCase() + " anteriormente.", false);
        }

        if ("APROVAR".equalsIgnoreCase(acao)) {
            credencial.setEstado(EstadoCredencial.APROVADA);
            credencialRepository.save(credencial);
            emailService.enviarNotificacaoSeguranca(credencial, securityEmails.split(","));
            return paginaHtmlResultado("Credencial Aprovada",
                "A credencial de <strong>" + credencial.getNome() + "</strong> foi aprovada. A equipa de segurança foi notificada.", true);
        } else if ("REJEITAR".equalsIgnoreCase(acao)) {
            credencial.setEstado(EstadoCredencial.REJEITADA);
            credencialRepository.save(credencial);
            emailService.enviarRejeicaoColaborador(credencial);
            return paginaHtmlResultado("Credencial Rejeitada",
                "A credencial de <strong>" + credencial.getNome() + "</strong> foi rejeitada. O colaborador foi notificado.", false);
        } else {
            throw new BusinessException("Ação inválida. Use APROVAR ou REJEITAR.");
        }
    }

    private String paginaHtmlResultado(String titulo, String mensagem, boolean sucesso) {
        String cor = sucesso ? "#28a745" : "#dc3545";
        String icone = sucesso ? "✓" : "✗";
        return """
                <!DOCTYPE html>
                <html lang="pt"><head><meta charset="UTF-8"><title>%s</title>
                <style>body{font-family:sans-serif;display:flex;justify-content:center;align-items:center;min-height:100vh;margin:0;background:#f0f2f5}
                .card{background:#fff;border-radius:8px;padding:48px 40px;text-align:center;max-width:480px;box-shadow:0 2px 16px rgba(0,0,0,.1)}
                .icon{width:72px;height:72px;border-radius:50%;background:%s;color:#fff;font-size:36px;display:flex;align-items:center;justify-content:center;margin:0 auto 20px}
                h1{color:#343a40;font-size:22px}p{color:#6c757d}</style></head>
                <body><div class="card"><div class="icon">%s</div><h1>%s</h1><p>%s</p></div></body></html>
                """.formatted(titulo, cor, icone, titulo, mensagem);
    }
}
