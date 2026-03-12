package home03.credenciais.services;

import home03.credenciais.entities.Credencial;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Email enviado ao colaborador após submissão do pré-registo */
    public void enviarConfirmacaoColaborador(Credencial c) {
        String assunto = "Pré-registo recebido – aguarda validação";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#1a73e8">Pré-registo recebido</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>O seu pré-registo de acesso foi recebido com sucesso e encontra-se <strong>a aguardar validação</strong> pelo responsável de mercado.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo de Acesso</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Val. Credencial</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px;color:#666;font-size:12px">Será notificado por email após a validação.</p>
                </body></html>
                """.formatted(c.getNome(), c.getEmpresa(), c.getTipo(), c.getDataValidadeCredencial());
        enviar(c.getEmail(), assunto, corpo);
    }

    /** Email enviado ao responsável com links de aprovação/rejeição */
    public void enviarPedidoValidacaoResponsavel(Credencial c) {
        String linkAprovar = baseUrl + "/api/public/validar?token=" + c.getTokenValidacao() + "&acao=APROVAR";
        String linkRejeitar = baseUrl + "/api/public/validar?token=" + c.getTokenValidacao() + "&acao=REJEITAR";

        String assunto = "Novo colaborador para validação – " + c.getNome();
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#1a73e8">Validação de credencial de acesso</h2>
                <p>Foi submetido um novo pré-registo de colaborador externo para a sua validação.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Nome</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Email</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Val. Credencial</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Val. Ficha Aptidão</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Nº Apólice</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Val. Seguro</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <div style="margin:30px 0;text-align:center">
                  <a href="%s" style="background:#28a745;color:#fff;padding:12px 30px;border-radius:4px;text-decoration:none;font-weight:bold;margin-right:20px">✓ APROVAR</a>
                  <a href="%s" style="background:#dc3545;color:#fff;padding:12px 30px;border-radius:4px;text-decoration:none;font-weight:bold">✗ REJEITAR</a>
                </div>
                <p style="color:#666;font-size:12px">Este link é de utilização única. Se não reconhece este pedido, ignore este email.</p>
                </body></html>
                """.formatted(
                    c.getNome(), c.getEmail(), c.getEmpresa(), c.getTipo(),
                    c.getDataValidadeCredencial(), c.getDataValidadeFichaAptidao(),
                    c.getNumApolice(), c.getDataValidadeSeguro(),
                    linkAprovar, linkRejeitar
                );
        enviar(c.getEmailResponsavel(), assunto, corpo);
    }

    /** Email enviado à equipa de segurança quando uma credencial é aprovada */
    public void enviarNotificacaoSeguranca(Credencial c, String[] emailsSeguranca) {
        String assunto = "Credencial APROVADA – " + c.getNome() + " pode entrar";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#28a745">✓ Credencial Aprovada</h2>
                <p>A credencial do seguinte colaborador foi <strong>aprovada</strong> e está autorizado a entrar.</p>
                <table style="border-collapse:collapse;width:100%%">
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Nome</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Empresa</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Tipo</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                  <tr><td style="padding:6px;border:1px solid #ddd;font-weight:bold">Val. Credencial</td><td style="padding:6px;border:1px solid #ddd">%s</td></tr>
                </table>
                <p style="margin-top:20px">Aceda ao <a href="%s/credenciais">Painel de Segurança</a> para confirmar a entrada.</p>
                </body></html>
                """.formatted(c.getNome(), c.getEmpresa(), c.getTipo(), c.getDataValidadeCredencial(), frontendUrl);

        for (String email : emailsSeguranca) {
            enviar(email.trim(), assunto, corpo);
        }
    }

    /** Email enviado ao colaborador quando a credencial é rejeitada */
    public void enviarRejeicaoColaborador(Credencial c) {
        String assunto = "Credencial não aprovada";
        String corpo = """
                <html><body style="font-family:sans-serif;color:#333;max-width:600px;margin:auto">
                <h2 style="color:#dc3545">Credencial não aprovada</h2>
                <p>Olá <strong>%s</strong>,</p>
                <p>Lamentamos informar que a sua credencial de acesso <strong>não foi aprovada</strong> pelo responsável de mercado.</p>
                <p>Para mais informações, contacte a equipa de receção.</p>
                </body></html>
                """.formatted(c.getNome());
        enviar(c.getEmail(), assunto, corpo);
    }

    private void enviar(String destinatario, String assunto, String corpo) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpo, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            // Loga o erro mas não bloqueia o fluxo principal
            System.err.println("[EmailService] Falha ao enviar email para " + destinatario + ": " + e.getMessage());
        }
    }
}
