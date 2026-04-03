package home03.credenciais.services;

import home03.credenciais.entities.ColaboradorExterno;
import home03.credenciais.repositories.ColaboradorRepository;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorService(ColaboradorRepository colaboradorRepository) {
        this.colaboradorRepository = colaboradorRepository;
    }

    @Transactional(readOnly = true)
    public Page<ColaboradorExterno> listar(Pageable pageable) {
        return colaboradorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ColaboradorExterno buscarPorId(UUID id) {
        return colaboradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public ColaboradorExterno buscarPorCodigo(String codigoInterno) {
        return colaboradorRepository.findByCodigoInterno(codigoInterno)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Colaborador não encontrado com código: " + codigoInterno));
    }
}
