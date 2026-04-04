package home03.credenciais.services;

import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.ColaboradorExterno;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.Empresa;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredencialServiceTest {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private FluxoAprovacaoService fluxoAprovacaoService;

    @InjectMocks
    private CredencialService credencialService;

    // --- listar ---

    @Test
    void listar_semFiltroDeEstado_deveUsarFindAll() {
        when(credencialRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(credencial(EstadoCredencial.APROVADA))));

        Page<CredencialDTO> resultado = credencialService.listar(null, Pageable.unpaged());

        assertThat(resultado.getContent()).hasSize(1);
        verify(credencialRepository).findAll(any(Pageable.class));
        verify(credencialRepository, never()).findByEstado(any(), any());
    }

    @Test
    void listar_comFiltroDeEstado_deveUsarFindByEstado() {
        when(credencialRepository.findByEstado(eq(EstadoCredencial.APROVADA), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(credencial(EstadoCredencial.APROVADA))));

        Page<CredencialDTO> resultado = credencialService.listar(EstadoCredencial.APROVADA, Pageable.unpaged());

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEstado()).isEqualTo(EstadoCredencial.APROVADA);
        verify(credencialRepository).findByEstado(eq(EstadoCredencial.APROVADA), any(Pageable.class));
        verify(credencialRepository, never()).findAll(any(Pageable.class));
    }

    // --- obterPorId ---

    @Test
    void obterPorId_existente_deveRetornarDTO() {
        Credencial c = credencial(EstadoCredencial.APROVADA);
        UUID id = (UUID) ReflectionTestUtils.getField(c, "id");
        when(credencialRepository.findById(id)).thenReturn(Optional.of(c));

        CredencialDTO resultado = credencialService.obterPorId(id);

        assertThat(resultado.getColaboradorNome()).isEqualTo("João Silva");
        assertThat(resultado.getEstado()).isEqualTo(EstadoCredencial.APROVADA);
        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void obterPorId_naoExiste_deveLancarResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(credencialRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credencialService.obterPorId(id))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining(id.toString());
    }

    // --- autorizarEntrada ---

    @Test
    void autorizarEntrada_credencialAprovada_deveDelegarAoFluxoERetornarDTO() {
        Credencial c = credencial(EstadoCredencial.APROVADA);
        UUID id = (UUID) ReflectionTestUtils.getField(c, "id");
        Credencial cAtualizado = credencial(EstadoCredencial.ENTRADA_AUTORIZADA);
        ReflectionTestUtils.setField(cAtualizado, "id", id);
        when(fluxoAprovacaoService.autorizarEntrada(id, "user@test.com")).thenReturn(cAtualizado);

        CredencialDTO resultado = credencialService.autorizarEntrada(id, "user@test.com");

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredencial.ENTRADA_AUTORIZADA);
        verify(fluxoAprovacaoService).autorizarEntrada(id, "user@test.com");
    }

    @Test
    void autorizarEntrada_estadoInvalido_devePropagaarBusinessException() {
        UUID id = UUID.randomUUID();
        when(fluxoAprovacaoService.autorizarEntrada(eq(id), any()))
            .thenThrow(new BusinessException("Transição inválida: credencial está em PENDENTE mas é necessário APROVADA para ir para ENTRADA_AUTORIZADA"));

        assertThatThrownBy(() -> credencialService.autorizarEntrada(id, "user@test.com"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("APROVADA");
    }

    @Test
    void autorizarEntrada_naoExiste_devePropagaarResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(fluxoAprovacaoService.autorizarEntrada(eq(id), any()))
            .thenThrow(new ResourceNotFoundException("Credencial não encontrada: " + id));

        assertThatThrownBy(() -> credencialService.autorizarEntrada(id, "user@test.com"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- helper ---

    private Credencial credencial(EstadoCredencial estado) {
        ColaboradorExterno ce = new ColaboradorExterno();
        ce.setCodigoInterno("CE-2026-0001");
        ce.setNome("João Silva");
        ce.setEmail("joao@empresa.com");
        ce.setDataNascimento(LocalDate.of(1990, 5, 20));

        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Teste");

        Credencial c = new Credencial();
        ReflectionTestUtils.setField(c, "id", UUID.randomUUID());
        c.setCodigoInterno("CD-2026-0001");
        c.setColaborador(ce);
        c.setEmpresa(empresa);
        c.setTipoColaborador(TipoColaborador.REPOSICAO);
        c.setEstado(estado);
        c.setDataInicio(LocalDate.now());
        c.setDataFim(LocalDate.now().plusMonths(6));
        return c;
    }
}
