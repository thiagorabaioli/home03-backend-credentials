package home03.credenciais.services;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.NotificacaoEmail;
import home03.credenciais.entities.TokenAprovacao;
import home03.credenciais.entities.enums.TipoNotificacao;
import home03.credenciais.repositories.NotificacaoEmailRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificacaoEmailRepository notificacaoRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${security.notification-emails}")
    private String securityEmails;

    public EmailService(JavaMailSender mailSender, NotificacaoEmailRepository notificacaoRepository) {
        this.mailSender = mailSender;
        this.notificacaoRepository = notificacaoRepository;
    }

    /** Primeiro registo: envia o código interno CE-YYYY-NNNN ao colaborador */
    public void enviarCodigoColaborador(Credencial c) {
        String nomeColaborador = c.getColaborador().getNome();
        String codigoCE = c.getColaborador().getCodigoInterno();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "O seu código de acesso – " + codigoCE;
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#1a73e8">Registo recebido</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>O seu registo de acesso foi recebido e encontra-se <strong>a aguardar validação</strong> pela equipa de segurança.</p>
                <div style="background:#f0f4ff;border:2px solid #1a73e8;border-radius:8px;padding:20px;text-align:center;margin:20px 0">
                  <p style="margin:0;font-size:12px;color:#666">O seu código de colaborador</p>
                  <p style="margin:8px 0;font-size:28px;font-weight:bold;letter-spacing:4px;color:#1a73e8">%s</p>
                  <p style="margin:0;font-size:12px;color:#666">Guarde este código — vai precisar dele para futuros registos</p>
                </div>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo de Acesso</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Validade</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px;color:#666;font-size:12px">Será notificado por email após a decisão.</p>
                </body></html>
                """.formatted(nomeColaborador, codigoCE, nomeEmpresa, c.getTipoColaborador(), c.getDataFim());
        enviar(c.getColaborador().getEmail(), assunto, corpo, c, TipoNotificacao.CONFIRMACAO_CE);
    }

    /** Confirmação ao CE após submissão do registo (CE já existente) */
    public void enviarConfirmacaoColaborador(Credencial c) {
        String nomeColaborador = c.getColaborador().getNome();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "Registo recebido – aguarda validação";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#1a73e8">Registo recebido</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>O seu registo de acesso foi recebido e encontra-se <strong>a aguardar validação</strong> pela equipa de segurança.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo de Acesso</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Validade</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px;color:#666;font-size:12px">Será notificado por email após a decisão.</p>
                </body></html>
                """.formatted(nomeColaborador, nomeEmpresa, c.getTipoColaborador(), c.getDataFim());
        enviar(c.getColaborador().getEmail(), assunto, corpo, c, TipoNotificacao.CONFIRMACAO_CE);
    }

    /** ES rejeita por dados inválidos — CE deve reiniciar o processo */
    public void enviarRejeicaoDadosInvalidosCE(Credencial c, String motivo) {
        String nomeColaborador = c.getColaborador().getNome();
        String assunto = "Registo não validado — dados inválidos";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#dc3545">Registo não validado</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>O seu registo de acesso não passou a validação inicial pela equipa de segurança. Será necessário <strong>reiniciar o processo</strong>.</p>
                <p><strong>Motivo:</strong> %s</p>
                <p style="margin-top:20px;color:#666;font-size:12px">Para submeter um novo registo, utilize o QR code no local de acesso. Se tiver dúvidas, contacte a receção.</p>
                </body></html>
                """.formatted(nomeColaborador, motivo != null ? motivo : "Dados em falta ou incorretos.");
        enviar(c.getColaborador().getEmail(), assunto, corpo, c, TipoNotificacao.REJEICAO_CE);
    }

    /** ES envia link de aprovação ao RM */
    public void enviarLinkAprovacaoRM(Credencial c, TokenAprovacao token) {
        String emailRM = c.getEmpresa().getResponsavel().getEmail();
        String nomeRM = c.getEmpresa().getResponsavel().getNome();
        String nomeColaborador = c.getColaborador().getNome();
        String nomeEmpresa = c.getEmpresa().getNome();

        String linkAprovar = baseUrl + "/aprovacao/" + token.getId() + "/aprovar";
        String linkRejeitar = baseUrl + "/aprovacao/" + token.getId() + "/rejeitar";

        String assunto = "Aprovação de acesso – " + nomeColaborador + " (" + nomeEmpresa + ")";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#1a73e8">Pedido de aprovação de acesso</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>Foi submetido um pedido de acesso para o seguinte colaborador da sua empresa. Por favor, confirme ou rejeite através dos botões abaixo.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Nome</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo de Acesso</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Validade</td><td style="padding:6px;border:1px solid #ddd">%s até %s</td></tr>
                </table>
                <div style="margin:30px 0;text-align:center">
                  <a href="%s" style="background:#28a745;color:#fff;padding:12px 30px;border-radius:4px;text-decoration:none;font-weight:bold;margin-right:20px">✓ APROVAR</a>
                  <a href="%s" style="background:#dc3545;color:#fff;padding:12px 30px;border-radius:4px;text-decoration:none;font-weight:bold">✗ REJEITAR</a>
                </div>
                <p style="color:#666;font-size:12px">Este link é válido por 72 horas e só pode ser utilizado uma vez.</p>
                </body></html>
                """.formatted(
                    nomeRM, nomeColaborador, nomeEmpresa,
                    c.getTipoColaborador(), c.getDataInicio(), c.getDataFim(),
                    linkAprovar, linkRejeitar
                );
        enviar(emailRM, assunto, corpo, c, TipoNotificacao.LINK_RM);
    }

    /** RM aprovou — notificar ES */
    public void enviarAprovacaoES(Credencial c) {
        String nomeColaborador = c.getColaborador().getNome();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "Credencial APROVADA – " + nomeColaborador;
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#28a745">✓ Credencial Aprovada pelo RM</h2>
                <p>O responsável de mercado <strong>aprovou</strong> o acesso do seguinte colaborador.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Nome</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Validade</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px">Aceda ao <a href="%s/credenciais">Painel de Segurança</a> para autorizar a entrada.</p>
                </body></html>
                """.formatted(nomeColaborador, nomeEmpresa, c.getTipoColaborador(), c.getDataFim(), frontendUrl);
        for (String email : securityEmails.split(",")) {
            enviar(email.trim(), assunto, corpo, c, TipoNotificacao.APROVACAO_ES);
        }
    }

    /** RM rejeitou — notificar ES com motivo */
    public void enviarRejeicaoRMParaES(Credencial c, String motivo) {
        String nomeColaborador = c.getColaborador().getNome();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "Credencial REJEITADA pelo RM – " + nomeColaborador;
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#dc3545">✗ Credencial Rejeitada pelo RM</h2>
                <p>O responsável de mercado <strong>rejeitou</strong> o pedido de acesso abaixo.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Nome</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Motivo</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px">Aceda ao <a href="%s/credenciais">Painel de Segurança</a> para consultar o registo.</p>
                </body></html>
                """.formatted(nomeColaborador, nomeEmpresa, c.getTipoColaborador(),
                    motivo != null ? motivo : "Não especificado", frontendUrl);
        for (String email : securityEmails.split(",")) {
            enviar(email.trim(), assunto, corpo, c, TipoNotificacao.REJEICAO_ES);
        }
    }

    /** Credencial transitou para EXPIRADA — notificar ES */
    public void enviarExpiracaoCredencial(Credencial c) {
        String nomeColaborador = c.getColaborador().getNome();
        String codigoCE = c.getColaborador().getCodigoInterno();
        String codigoCD = c.getCodigoInterno();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "Credencial EXPIRADA – " + codigoCD + " (" + nomeColaborador + ")";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#dc3545">Credencial Expirada</h2>
                <p>A credencial abaixo expirou e foi transitada automaticamente para o estado <strong>EXPIRADA</strong>.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Credencial</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Colaborador</td><td style="padding:6px;border:1px solid #ddd">%s (%s)</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Data de expiração</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px">Aceda ao <a href="%s/credenciais">Painel de Segurança</a> para consultar o registo.</p>
                </body></html>
                """.formatted(codigoCD, nomeColaborador, codigoCE, nomeEmpresa,
                    c.getTipoColaborador(), c.getDataFim(), frontendUrl);
        for (String email : securityEmails.split(",")) {
            enviar(email.trim(), assunto, corpo, c, TipoNotificacao.EXPIRACAO_CREDENCIAL);
        }
    }

    /** Alerta à ES: seguro da credencial expira nos próximos 30 dias */
    public void enviarAlertaExpiracaoSeguro(Credencial c) {
        String nomeColaborador = c.getColaborador().getNome();
        String codigoCD = c.getCodigoInterno();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "Alerta: seguro a expirar – " + codigoCD + " (" + nomeColaborador + ")";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#fd7e14">⚠ Seguro a expirar</h2>
                <p>O seguro associado à credencial abaixo expira em breve. Por favor, solicite a renovação ao colaborador.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Credencial</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Colaborador</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Validade do seguro</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px">Aceda ao <a href="%s/credenciais">Painel de Segurança</a> para consultar o registo.</p>
                </body></html>
                """.formatted(codigoCD, nomeColaborador, nomeEmpresa,
                    c.getSeguro() != null ? c.getSeguro().getDataFim() : "N/D", frontendUrl);
        for (String email : securityEmails.split(",")) {
            enviar(email.trim(), assunto, corpo, c, TipoNotificacao.ALERTA_EXPIRACAO_SEGURO);
        }
    }

    /** Alerta à ES: FAM da credencial expira nos próximos 30 dias */
    public void enviarAlertaExpiracaoFam(Credencial c) {
        String nomeColaborador = c.getColaborador().getNome();
        String codigoCD = c.getCodigoInterno();
        String nomeEmpresa = c.getEmpresa().getNome();
        String assunto = "Alerta: ficha médica a expirar – " + codigoCD + " (" + nomeColaborador + ")";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#fd7e14">⚠ Ficha de Aptidão Médica a expirar</h2>
                <p>A ficha de aptidão médica associada à credencial abaixo expira em breve. Por favor, solicite a renovação ao colaborador.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Credencial</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Colaborador</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Validade da FAM</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px">Aceda ao <a href="%s/credenciais">Painel de Segurança</a> para consultar o registo.</p>
                </body></html>
                """.formatted(codigoCD, nomeColaborador, nomeEmpresa,
                    c.getFichaAptidaoMedica() != null ? c.getFichaAptidaoMedica().getDataValidade() : "N/D", frontendUrl);
        for (String email : securityEmails.split(",")) {
            enviar(email.trim(), assunto, corpo, c, TipoNotificacao.ALERTA_EXPIRACAO_FAM);
        }
    }

    private void enviar(String destinatario, String assunto, String corpo,
                        Credencial credencial, TipoNotificacao tipo) {
        boolean sucesso = true;
        String erro = null;
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo, true);
            mailSender.send(msg);
        } catch (Exception e) {
            sucesso = false;
            erro = e.getMessage();
            System.err.println("[EmailService] Falha ao enviar email para " + destinatario + ": " + e.getMessage());
        } finally {
            NotificacaoEmail notificacao = new NotificacaoEmail(credencial, destinatario, assunto, tipo, sucesso);
            if (!sucesso) notificacao.setErro(erro);
            notificacaoRepository.save(notificacao);
        }
    }
}
