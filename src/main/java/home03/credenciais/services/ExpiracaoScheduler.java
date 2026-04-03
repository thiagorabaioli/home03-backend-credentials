package home03.credenciais.services;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.CredencialRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpiracaoScheduler {

    private static final List<EstadoCredencial> ESTADOS_FINAIS = List.of(
            EstadoCredencial.EXPIRADA,
            EstadoCredencial.REJEITADA_ES,
            EstadoCredencial.REJEITADA_RM,
            EstadoCredencial.INTERDITO,
            EstadoCredencial.OUTRO
    );

    private static final int DIAS_ALERTA_ANTECIPADO = 30;

    private final CredencialRepository credencialRepository;
    private final AuditService auditService;
    private final EmailService emailService;

    public ExpiracaoScheduler(CredencialRepository credencialRepository,
                               AuditService auditService,
                               EmailService emailService) {
        this.credencialRepository = credencialRepository;
        this.auditService = auditService;
        this.emailService = emailService;
    }

    /** Executa todos os dias às 04h00 */
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void verificarExpiracoes() {
        LocalDate hoje = LocalDate.now();

        transitarCredenciaisExpiradas(hoje);
        alertarSeguroAExpirar(hoje);
        alertarFamAExpirar(hoje);
    }

    private void transitarCredenciaisExpiradas(LocalDate hoje) {
        List<Credencial> expiradas = credencialRepository.findExpiradas(hoje, ESTADOS_FINAIS);
        for (Credencial c : expiradas) {
            EstadoCredencial estadoAnterior = c.getEstado();
            c.setEstado(EstadoCredencial.EXPIRADA);
            credencialRepository.save(c);
            auditService.registarTransicao(c, estadoAnterior, EstadoCredencial.EXPIRADA,
                    "SISTEMA", "Credencial expirada automaticamente em " + hoje);
            emailService.enviarExpiracaoCredencial(c);
        }
    }

    private void alertarSeguroAExpirar(LocalDate hoje) {
        LocalDate limite = hoje.plusDays(DIAS_ALERTA_ANTECIPADO);
        List<Credencial> comSeguroAExpirar = credencialRepository
                .findComSeguroAExpirar(hoje, limite, ESTADOS_FINAIS);
        for (Credencial c : comSeguroAExpirar) {
            emailService.enviarAlertaExpiracaoSeguro(c);
        }
    }

    private void alertarFamAExpirar(LocalDate hoje) {
        LocalDate limite = hoje.plusDays(DIAS_ALERTA_ANTECIPADO);
        List<Credencial> comFamAExpirar = credencialRepository
                .findComFamAExpirar(hoje, limite, ESTADOS_FINAIS);
        for (Credencial c : comFamAExpirar) {
            emailService.enviarAlertaExpiracaoFam(c);
        }
    }
}
