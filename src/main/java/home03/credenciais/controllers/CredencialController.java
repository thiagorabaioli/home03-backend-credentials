package home03.credenciais.controllers;

import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.services.CredencialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/credenciais")
public class CredencialController {

    private final CredencialService credencialService;

    public CredencialController(CredencialService credencialService) {
        this.credencialService = credencialService;
    }

    /** GET /api/credenciais?estado=AGUARDA_VALIDACAO_ES&page=0&size=10 */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<Page<CredencialDTO>> listar(
            @RequestParam(required = false) EstadoCredencial estado,
            Pageable pageable) {
        return ResponseEntity.ok(credencialService.listar(estado, pageable));
    }

    /** GET /api/credenciais/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<CredencialDTO> obterPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(credencialService.obterPorId(id));
    }

    /**
     * POST /api/credenciais/{id}/rejeitar-es
     * ES rejeita por dados inválidos — CE deve reiniciar o processo.
     * Body: { "motivo": "..." }
     */
    @PostMapping("/{id}/rejeitar-es")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<CredencialDTO> rejeitarPorDadosInvalidos(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal Jwt jwt) {
        String responsavel = jwt.getSubject();
        String motivo = body.get("motivo");
        return ResponseEntity.ok(credencialService.rejeitarPorDadosInvalidos(id, responsavel, motivo));
    }

    /**
     * POST /api/credenciais/{id}/enviar-rm
     * ES valida e envia link de aprovação ao RM.
     */
    @PostMapping("/{id}/enviar-rm")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<Void> enviarParaAprovacaoRM(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        credencialService.enviarParaAprovacaoRM(id, jwt.getSubject());
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/credenciais/{id}/autorizar-entrada
     * ES autoriza entrada após aprovação do RM.
     */
    @PostMapping("/{id}/autorizar-entrada")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<CredencialDTO> autorizarEntrada(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(credencialService.autorizarEntrada(id, jwt.getSubject()));
    }

    /**
     * DELETE /api/credenciais/{id}
     * Eliminação permanente — restrita a ROLE_ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        credencialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
