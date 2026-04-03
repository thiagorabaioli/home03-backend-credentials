package home03.credenciais.controllers;

import home03.credenciais.services.FluxoAprovacaoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Endpoints públicos (sem login) acedidos pelo Responsável de Mercado via link no email.
 * O token UUID no path é a única forma de autorização — válido 72h e de uso único.
 */
@RestController
@RequestMapping("/aprovacao")
public class AprovacaoController {

    private final FluxoAprovacaoService fluxoAprovacaoService;

    public AprovacaoController(FluxoAprovacaoService fluxoAprovacaoService) {
        this.fluxoAprovacaoService = fluxoAprovacaoService;
    }

    /**
     * GET /aprovacao/{tokenId}/aprovar
     * RM clica "APROVAR" no email — devolve página HTML de confirmação.
     */
    @GetMapping(value = "/{tokenId}/aprovar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> aprovar(@PathVariable UUID tokenId) {
        fluxoAprovacaoService.aprovarPeloRM(tokenId);
        String html = paginaResultado("Credencial Aprovada",
                "O acesso foi <strong>aprovado</strong>. A equipa de segurança foi notificada.", true);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                .body(html);
    }

    /**
     * GET /aprovacao/{tokenId}/rejeitar
     * RM clica "REJEITAR" — pode incluir motivo via query param.
     */
    @GetMapping(value = "/{tokenId}/rejeitar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> rejeitar(
            @PathVariable UUID tokenId,
            @RequestParam(required = false) String motivo) {
        fluxoAprovacaoService.rejeitarPeloRM(tokenId, motivo);
        String html = paginaResultado("Credencial Rejeitada",
                "O acesso foi <strong>rejeitado</strong>. A equipa de segurança foi notificada.", false);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                .body(html);
    }

    private String paginaResultado(String titulo, String mensagem, boolean sucesso) {
        String cor = sucesso ? "#2f9e44" : "#e03131";
        String corClara = sucesso ? "#ebfbee" : "#fff5f5";
        String icone = sucesso ? "✓" : "✗";
        String badge = sucesso ? "Concluído" : "Rejeitado";
        return """
                <!DOCTYPE html>
                <html lang="pt"><head><meta charset="UTF-8"><title>%s</title>
                <style>
                *{box-sizing:border-box;margin:0;padding:0}
                body{font-family:'Segoe UI',Arial,sans-serif;display:flex;justify-content:center;align-items:center;min-height:100vh;background:#f0f4f8}
                .card{background:#ffffff;border-radius:12px;padding:52px 44px;text-align:center;max-width:460px;width:90%%;box-shadow:0 1px 4px rgba(0,0,0,.06),0 4px 20px rgba(0,0,0,.06)}
                .icon{width:76px;height:76px;border-radius:50%%;background:%s;color:#fff;font-size:32px;font-weight:bold;display:flex;align-items:center;justify-content:center;margin:0 auto 24px}
                .badge{display:inline-block;background:%s;color:%s;font-size:12px;font-weight:600;letter-spacing:.5px;text-transform:uppercase;padding:4px 12px;border-radius:20px;margin-bottom:16px}
                h1{color:#1a202c;font-size:20px;font-weight:600;margin-bottom:12px}
                p{color:#718096;font-size:15px;line-height:1.6}
                </style></head>
                <body><div class="card">
                  <div class="icon">%s</div>
                  <div class="badge">%s</div>
                  <h1>%s</h1>
                  <p>%s</p>
                </div></body></html>
                """.formatted(titulo, cor, corClara, cor, icone, badge, titulo, mensagem);
    }
}
