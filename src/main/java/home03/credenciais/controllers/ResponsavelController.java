package home03.credenciais.controllers;

import home03.credenciais.entities.ResponsavelMercado;
import home03.credenciais.services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    private final EmpresaService empresaService;

    public ResponsavelController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    /** GET /api/responsaveis?page=0&size=10 */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<ResponsavelMercado>> listar(Pageable pageable) {
        return ResponseEntity.ok(empresaService.listarResponsaveis(pageable));
    }

    /** GET /api/responsaveis/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponsavelMercado> obterPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.buscarResponsavelPorId(id));
    }

    /** POST /api/responsaveis */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponsavelMercado> criar(@Valid @RequestBody ResponsavelMercado responsavel) {
        ResponsavelMercado criado = empresaService.criarResponsavel(responsavel);
        return ResponseEntity.created(URI.create("/api/responsaveis/" + criado.getId())).body(criado);
    }

    /** PUT /api/responsaveis/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponsavelMercado> actualizar(
            @PathVariable UUID id, @Valid @RequestBody ResponsavelMercado responsavel) {
        return ResponseEntity.ok(empresaService.actualizarResponsavel(id, responsavel));
    }
}
