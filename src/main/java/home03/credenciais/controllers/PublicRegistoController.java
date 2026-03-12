package home03.credenciais.controllers;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.entities.Credencial;
import home03.credenciais.services.RegistoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicRegistoController {

    private final RegistoService registoService;

    public PublicRegistoController(RegistoService registoService) {
        this.registoService = registoService;
    }

    /**
     * POST /api/public/registo
     * Recebe o formulário de pré-registo (multipart/form-data).
     * Não requer autenticação.
     */
    @PostMapping(value = "/registo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> submeterPreRegisto(
            @Valid @ModelAttribute RegistoPublicoDTO dto,
            @RequestParam(value = "documentos", required = false) List<MultipartFile> documentos) {
        registoService.processarPreRegisto(dto, documentos);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/public/validar?token=...&acao=APROVAR|REJEITAR
     * Link clicado pelo responsável no email. Devolve HTML directamente.
     */
    @GetMapping(value = "/validar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> validarCredencial(
            @RequestParam String token,
            @RequestParam String acao) {
        String html = registoService.processarValidacao(token, acao);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
            .body(html);
    }
}
