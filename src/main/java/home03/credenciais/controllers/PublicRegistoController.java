package home03.credenciais.controllers;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.services.RegistoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * documentoSeguro e documentoFicha são os ficheiros obrigatórios.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registar(
            @Valid @ModelAttribute RegistoPublicoDTO dto,
            @RequestParam(value = "documentoSeguro", required = false) MultipartFile documentoSeguro,
            @RequestParam(value = "documentoFicha", required = false) MultipartFile documentoFicha) {
        registoService.registar(dto, documentoSeguro, documentoFicha);
        return ResponseEntity.ok().build();
    }
}
