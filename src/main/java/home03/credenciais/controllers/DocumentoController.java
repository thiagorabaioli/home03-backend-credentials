package home03.credenciais.controllers;

import home03.credenciais.entities.Documento;
import home03.credenciais.services.DocumentoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    /**
     * GET /api/documentos/{id}
     * Serve o ficheiro via stream. A ES usa este endpoint para visualizar documentos.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        Documento doc = documentoService.buscarMetadata(id);
        Resource resource = documentoService.carregar(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getNomeOriginal() + "\"")
                .body(resource);
    }
}
