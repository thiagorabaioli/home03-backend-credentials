package home03.credenciais.services;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.entities.ColaboradorExterno;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.Empresa;
import home03.credenciais.entities.ResponsavelMercado;
import home03.credenciais.entities.enums.DiaSemana;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.ResultadoFicha;
import home03.credenciais.entities.enums.TipoColaborador;
import home03.credenciais.repositories.ColaboradorRepository;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.repositories.EmpresaRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistoServiceTest {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private DocumentoService documentoService;

    @Mock
    private AuditService auditService;

    @Mock
    private EmailService emailService;

    @Mock
    private CodigoService codigoService;

    @InjectMocks
    private RegistoService registoService;

    // --- registar: erro empresa ---

    @Test
    void registar_empresaNaoEncontrada_deveLancarResourceNotFoundException() {
        RegistoPublicoDTO dto = dtoValido();
        when(empresaRepository.findById(dto.getEmpresaId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registoService.registar(dto, null, null))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Empresa");
    }

    @Test
    void registar_empresaSemResponsavel_deveLancarBusinessException() {
        RegistoPublicoDTO dto = dtoValido();
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Teste");
        // responsavel null por omissão
        when(empresaRepository.findById(dto.getEmpresaId())).thenReturn(Optional.of(empresa));

        assertThatThrownBy(() -> registoService.registar(dto, null, null))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("responsável");
    }

    // --- registar: erro código interno ---

    @Test
    void registar_codigoInternoDesconhecido_deveLancarBusinessException() {
        RegistoPublicoDTO dto = dtoValido();
        dto.setCodigoInterno("CE-2026-9999");
        when(empresaRepository.findById(dto.getEmpresaId())).thenReturn(Optional.of(empresaComResponsavel()));
        when(colaboradorRepository.findByCodigoInterno("CE-2026-9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registoService.registar(dto, null, null))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("CE-2026-9999");
    }

    // --- registar: fluxo novo CE ---

    @Test
    void registar_novoCE_deveSalvarCredencialEnviarEmailComCodigo() {
        RegistoPublicoDTO dto = dtoValido();
        when(empresaRepository.findById(dto.getEmpresaId())).thenReturn(Optional.of(empresaComResponsavel()));
        when(codigoService.gerarCodigoCE()).thenReturn("CE-2026-0001");
        when(codigoService.gerarCodigoCD()).thenReturn("CD-2026-0001");
        when(colaboradorRepository.save(any())).thenAnswer(inv -> {
            ColaboradorExterno ce = inv.getArgument(0);
            ReflectionTestUtils.setField(ce, "id", UUID.randomUUID());
            return ce;
        });
        when(credencialRepository.existsByColaboradorCodigoInternoAndEmpresaIdAndTipoColaboradorAndEstadoIn(
            any(), any(), any(), any())).thenReturn(false);
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Credencial resultado = registoService.registar(dto, null, null);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredencial.AGUARDA_VALIDACAO_ES);
        assertThat(resultado.getCodigoInterno()).isEqualTo("CD-2026-0001");
        verify(emailService).enviarCodigoColaborador(any());
        verify(auditService).registarTransicao(any(),
            eq(EstadoCredencial.PENDENTE),
            eq(EstadoCredencial.AGUARDA_VALIDACAO_ES),
            eq("SISTEMA"), any());
    }

    @Test
    void registar_ceExistente_deveSalvarCredencialEEnviarEmailConfirmacao() {
        RegistoPublicoDTO dto = dtoValido();
        dto.setCodigoInterno("CE-2026-0001");
        ColaboradorExterno ce = colaborador("CE-2026-0001");
        when(empresaRepository.findById(dto.getEmpresaId())).thenReturn(Optional.of(empresaComResponsavel()));
        when(colaboradorRepository.findByCodigoInterno("CE-2026-0001")).thenReturn(Optional.of(ce));
        when(codigoService.gerarCodigoCD()).thenReturn("CD-2026-0002");
        when(credencialRepository.existsByColaboradorCodigoInternoAndEmpresaIdAndTipoColaboradorAndEstadoIn(
            any(), any(), any(), any())).thenReturn(false);
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Credencial resultado = registoService.registar(dto, null, null);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredencial.AGUARDA_VALIDACAO_ES);
        verify(emailService).enviarConfirmacaoColaborador(any());
        verify(emailService, never()).enviarCodigoColaborador(any());
    }

    // --- helpers ---

    private RegistoPublicoDTO dtoValido() {
        RegistoPublicoDTO dto = new RegistoPublicoDTO();
        dto.setNome("João Silva");
        dto.setEmail("joao@empresa.com");
        dto.setDataNascimento(LocalDate.of(1990, 5, 20));
        dto.setEmpresaId(UUID.randomUUID());
        dto.setTipoColaborador(TipoColaborador.REPOSICAO);
        dto.setDataInicio(LocalDate.now());
        dto.setDataFim(LocalDate.now().plusMonths(6));
        dto.setApolice("AP001");
        dto.setSeguradora("Seguros PT");
        dto.setSeguroDataInicio(LocalDate.now());
        dto.setSeguroDataFim(LocalDate.now().plusMonths(12));
        dto.setFichaDataEmissao(LocalDate.now());
        dto.setFichaDataValidade(LocalDate.now().plusMonths(6));
        dto.setFichaResultado(ResultadoFicha.APTO);
        dto.setDiasSemana(Set.of(DiaSemana.SEGUNDA));
        dto.setHoraEntrada(LocalTime.of(9, 0));
        dto.setHoraSaida(LocalTime.of(17, 0));
        return dto;
    }

    private Empresa empresaComResponsavel() {
        ResponsavelMercado resp = new ResponsavelMercado();
        resp.setNome("Responsável");
        resp.setEmail("resp@empresa.com");

        Empresa empresa = new Empresa();
        empresa.setNome("Empresa Teste");
        empresa.setResponsavel(resp);
        ReflectionTestUtils.setField(empresa, "id", UUID.randomUUID());
        return empresa;
    }

    private ColaboradorExterno colaborador(String codigo) {
        ColaboradorExterno ce = new ColaboradorExterno();
        ce.setCodigoInterno(codigo);
        ce.setNome("João Silva");
        ce.setEmail("joao@empresa.com");
        ce.setDataNascimento(LocalDate.of(1990, 5, 20));
        ReflectionTestUtils.setField(ce, "id", UUID.randomUUID());
        return ce;
    }
}
