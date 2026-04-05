package home03.credenciais.controllers;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.services.RegistoService;
import home03.credenciais.services.exceptions.BusinessException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/public/registo")
public class PublicRegistoController {

    private final RegistoService registoService;

    public PublicRegistoController(RegistoService registoService) {
        this.registoService = registoService;
    }

    /**
     * POST /public/registo
     * Recebe o formulário de registo do CE (multipart/form-data). Sem autenticação.
     * Aceita até 3 ficheiros genéricos (documentos). Pelo menos 1 é obrigatório.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registar(
            @Valid @ModelAttribute RegistoPublicoDTO dto,
            @RequestParam(value = "documentos", required = false) List<MultipartFile> documentos) {

        if (documentos == null) {
            documentos = new ArrayList<>();
        }
        // Filtrar ficheiros vazios
        documentos = documentos.stream()
                .filter(f -> f != null && !f.isEmpty())
                .toList();

        if (documentos.isEmpty()) {
            throw new BusinessException("É obrigatório anexar pelo menos 1 documento.");
        }
        if (documentos.size() > 3) {
            throw new BusinessException("Máximo de 3 documentos permitidos.");
        }

        registoService.registar(dto, documentos);
        return ResponseEntity.ok().build();
    }
}
