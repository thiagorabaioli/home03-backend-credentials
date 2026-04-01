package home03.credenciais.services;

import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.Credencial;
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

        assertThat(resultado.getNome()).isEqualTo("João Silva");
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
    void autorizarEntrada_credencialAprovada_deveMudarEstadoParaEntradaAutorizada() {
        Credencial c = credencial(EstadoCredencial.APROVADA);
        UUID id = (UUID) ReflectionTestUtils.getField(c, "id");
        when(credencialRepository.findById(id)).thenReturn(Optional.of(c));
        when(credencialRepository.save(c)).thenReturn(c);

        CredencialDTO resultado = credencialService.autorizarEntrada(id);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredencial.ENTRADA_AUTORIZADA);
        verify(credencialRepository).save(c);
    }

    @Test
    void autorizarEntrada_estadoPendente_deveLancarBusinessException() {
        Credencial c = credencial(EstadoCredencial.PENDENTE);
        UUID id = (UUID) ReflectionTestUtils.getField(c, "id");
        when(credencialRepository.findById(id)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> credencialService.autorizarEntrada(id))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("APROVADA");

        verify(credencialRepository, never()).save(any());
    }

    @Test
    void autorizarEntrada_estadoRejeitada_deveLancarBusinessException() {
        Credencial c = credencial(EstadoCredencial.REJEITADA);
        UUID id = (UUID) ReflectionTestUtils.getField(c, "id");
        when(credencialRepository.findById(id)).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> credencialService.autorizarEntrada(id))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void autorizarEntrada_naoExiste_deveLancarResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(credencialRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credencialService.autorizarEntrada(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- helper ---

    private Credencial credencial(EstadoCredencial estado) {
        Credencial c = new Credencial();
        ReflectionTestUtils.setField(c, "id", UUID.randomUUID());
        c.setNome("João Silva");
        c.setEmail("joao@empresa.com");
        c.setDataNascimento(LocalDate.of(1990, 5, 20));
        c.setEmpresa("Empresa Teste");
        c.setTipo(TipoColaborador.REPOSICAO);
        c.setEmailResponsavel("resp@empresa.com");
        c.setDataValidadeCredencial(LocalDate.now().plusMonths(6));
        c.setDataValidadeFichaAptidao(LocalDate.now().plusMonths(6));
        c.setNumApolice("AP001");
        c.setDataValidadeSeguro(LocalDate.now().plusMonths(12));
        c.setEstado(estado);
        return c;
    }
}
