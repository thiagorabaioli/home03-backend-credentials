package home03.credenciais.services;

import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CredencialService {

    private final CredencialRepository credencialRepository;

    public CredencialService(CredencialRepository credencialRepository) {
        this.credencialRepository = credencialRepository;
    }

    /** Lista credenciais paginadas, com filtro opcional por estado */
    @Transactional(readOnly = true)
    public Page<CredencialDTO> listar(EstadoCredencial estado, Pageable pageable) {
        Page<Credencial> page = (estado != null)
            ? credencialRepository.findByEstado(estado, pageable)
            : credencialRepository.findAll(pageable);
        return page.map(CredencialDTO::new);
    }

    /** Obtém uma credencial por ID */
    @Transactional(readOnly = true)
    public CredencialDTO obterPorId(UUID id) {
        Credencial c = credencialRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada: " + id));
        return new CredencialDTO(c);
    }

    /**
     * Autoriza a entrada de um colaborador com credencial APROVADA.
     * Muda estado para ENTRADA_AUTORIZADA.
     */
    @Transactional
    public CredencialDTO autorizarEntrada(UUID id) {
        Credencial c = credencialRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada: " + id));

        if (c.getEstado() != EstadoCredencial.APROVADA) {
            throw new BusinessException("Só é possível autorizar entrada de credenciais com estado APROVADA. Estado actual: " + c.getEstado());
        }

        c.setEstado(EstadoCredencial.ENTRADA_AUTORIZADA);
        return new CredencialDTO(credencialRepository.save(c));
    }
}
