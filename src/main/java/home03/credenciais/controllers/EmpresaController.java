package home03.credenciais.controllers;

import home03.credenciais.entities.Empresa;
import home03.credenciais.services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    /** GET /api/empresas — lista paginada (admin) */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<Empresa>> listar(Pageable pageable) {
        return ResponseEntity.ok(empresaService.listarEmpresas(pageable));
    }

    /** GET /api/empresas/todas — lista completa para selects no formulário público */
    @GetMapping("/todas")
    public ResponseEntity<List<Empresa>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodasEmpresas());
    }

    /** GET /api/empresas/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Empresa> obterPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.buscarEmpresaPorId(id));
    }

    /** POST /api/empresas */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Empresa> criar(@Valid @RequestBody Empresa empresa) {
        Empresa criada = empresaService.criarEmpresa(empresa);
        return ResponseEntity.created(URI.create("/api/empresas/" + criada.getId())).body(criada);
    }

    /** PUT /api/empresas/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Empresa> actualizar(@PathVariable UUID id, @Valid @RequestBody Empresa empresa) {
        return ResponseEntity.ok(empresaService.actualizarEmpresa(id, empresa));
    }

    /** PUT /api/empresas/{id}/responsavel/{responsavelId} */
    @PutMapping("/{id}/responsavel/{responsavelId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> associarResponsavel(@PathVariable UUID id, @PathVariable UUID responsavelId) {
        empresaService.associarResponsavel(id, responsavelId);
        return ResponseEntity.ok().build();
    }
}
