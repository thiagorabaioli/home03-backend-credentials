package home03.credenciais.services;

import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CredencialService {

    private final CredencialRepository credencialRepository;
    private final FluxoAprovacaoService fluxoAprovacaoService;

    public CredencialService(CredencialRepository credencialRepository,
                             FluxoAprovacaoService fluxoAprovacaoService) {
        this.credencialRepository = credencialRepository;
        this.fluxoAprovacaoService = fluxoAprovacaoService;
    }

    @Transactional(readOnly = true)
    public Page<CredencialDTO> listar(EstadoCredencial estado, Pageable pageable) {
        Page<Credencial> page = (estado != null)
                ? credencialRepository.findByEstado(estado, pageable)
                : credencialRepository.findAll(pageable);
        return page.map(CredencialDTO::new);
    }

    @Transactional(readOnly = true)
    public CredencialDTO obterPorId(UUID id) {
        Credencial c = credencialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada: " + id));
        return new CredencialDTO(c);
    }

    @Transactional
    public CredencialDTO rejeitarPorDadosInvalidos(UUID id, String responsavel, String motivo) {
        return new CredencialDTO(fluxoAprovacaoService.rejeitarPorDadosInvalidos(id, responsavel, motivo));
    }

    @Transactional
    public void enviarParaAprovacaoRM(UUID id, String responsavel) {
        fluxoAprovacaoService.enviarParaAprovacaoRM(id, responsavel);
    }

    @Transactional
    public CredencialDTO autorizarEntrada(UUID id, String responsavel) {
        return new CredencialDTO(fluxoAprovacaoService.autorizarEntrada(id, responsavel));
    }

    @Transactional
    public void eliminar(UUID id) {
        Credencial c = credencialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credencial não encontrada: " + id));
        credencialRepository.delete(c);
    }
}
