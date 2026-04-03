package home03.credenciais.controllers;

import home03.credenciais.entities.ColaboradorExterno;
import home03.credenciais.services.ColaboradorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    /** GET /api/colaboradores?page=0&size=10 */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<Page<ColaboradorExterno>> listar(Pageable pageable) {
        return ResponseEntity.ok(colaboradorService.listar(pageable));
    }

    /** GET /api/colaboradores/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<ColaboradorExterno> obterPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(colaboradorService.buscarPorId(id));
    }

    /** GET /api/colaboradores/codigo/{codigo} */
    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_VIGILANTE')")
    public ResponseEntity<ColaboradorExterno> obterPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(colaboradorService.buscarPorCodigo(codigo));
    }
}
