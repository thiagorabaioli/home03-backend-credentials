package home03.credenciais.services;

import home03.credenciais.entities.Empresa;
import home03.credenciais.entities.ResponsavelMercado;
import home03.credenciais.repositories.EmpresaRepository;
import home03.credenciais.repositories.ResponsavelRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ResponsavelRepository responsavelRepository;

    public EmpresaService(EmpresaRepository empresaRepository,
                          ResponsavelRepository responsavelRepository) {
        this.empresaRepository = empresaRepository;
        this.responsavelRepository = responsavelRepository;
    }

    // --- Empresa ---

    @Transactional(readOnly = true)
    public Page<Empresa> listarEmpresas(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarTodasEmpresas() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresaPorId(UUID id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada: " + id));
    }

    @Transactional
    public Empresa criarEmpresa(Empresa empresa) {
        if (empresa.getNif() != null && empresaRepository.existsByNif(empresa.getNif())) {
            throw new BusinessException("Já existe uma empresa com o NIF: " + empresa.getNif());
        }
        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa actualizarEmpresa(UUID id, Empresa dados) {
        Empresa empresa = buscarEmpresaPorId(id);
        empresa.setNome(dados.getNome());
        empresa.setMorada(dados.getMorada());
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void associarResponsavel(UUID empresaId, UUID responsavelId) {
        Empresa empresa = buscarEmpresaPorId(empresaId);
        ResponsavelMercado responsavel = buscarResponsavelPorId(responsavelId);
        empresa.setResponsavel(responsavel);
        empresaRepository.save(empresa);
    }

    // --- Responsável de Mercado ---

    @Transactional(readOnly = true)
    public Page<ResponsavelMercado> listarResponsaveis(Pageable pageable) {
        return responsavelRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ResponsavelMercado buscarResponsavelPorId(UUID id) {
        return responsavelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado: " + id));
    }

    @Transactional
    public ResponsavelMercado criarResponsavel(ResponsavelMercado responsavel) {
        if (responsavelRepository.existsByEmail(responsavel.getEmail())) {
            throw new BusinessException("Já existe um responsável com o email: " + responsavel.getEmail());
        }
        return responsavelRepository.save(responsavel);
    }

    @Transactional
    public ResponsavelMercado actualizarResponsavel(UUID id, ResponsavelMercado dados) {
        ResponsavelMercado responsavel = buscarResponsavelPorId(id);
        responsavel.setNome(dados.getNome());
        responsavel.setTelefone(dados.getTelefone());
        responsavel.setAtivo(dados.isAtivo());
        return responsavelRepository.save(responsavel);
    }
}
