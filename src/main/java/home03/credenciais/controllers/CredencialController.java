package home03.credenciais.controllers;

import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.services.CredencialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/credenciais")
public class CredencialController {

    private final CredencialService credencialService;

    public CredencialController(CredencialService credencialService) {
        this.credencialService = credencialService;
    }

    /**
     * GET /api/credenciais?estado=APROVADA&page=0&size=10
     * Lista credenciais paginadas. Requer autenticação.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE', 'ROLE_SECURITY')")
    public ResponseEntity<Page<CredencialDTO>> listar(
            @RequestParam(required = false) EstadoCredencial estado,
            Pageable pageable) {
        return ResponseEntity.ok(credencialService.listar(estado, pageable));
    }

    /**
     * GET /api/credenciais/{id}
     * Detalhes de uma credencial.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE', 'ROLE_SECURITY')")
    public ResponseEntity<CredencialDTO> obterPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(credencialService.obterPorId(id));
    }

    /**
     * POST /api/credenciais/{id}/autorizar-entrada
     * A segurança regista a entrada do colaborador.
     */
    @PostMapping("/{id}/autorizar-entrada")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE', 'ROLE_SECURITY')")
    public ResponseEntity<CredencialDTO> autorizarEntrada(@PathVariable UUID id) {
        return ResponseEntity.ok(credencialService.autorizarEntrada(id));
    }
}
