package home03.credenciais.services;

import home03.credenciais.dto.RegistoPublicoDTO;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;
import home03.credenciais.repositories.CredencialRepository;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistoServiceTest {

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegistoService registoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(registoService, "securityEmails", "seg@test.com");
    }

    // --- processarPreRegisto ---

    @Test
    void processarPreRegisto_semFicheiros_deveSalvarEEnviarEmails() {
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Credencial resultado = registoService.processarPreRegisto(dtoValido(), null);

        assertThat(resultado.getEstado()).isEqualTo(EstadoCredencial.AGUARDA_VALIDACAO);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        verify(credencialRepository, times(2)).save(any());
        verify(emailService).enviarConfirmacaoColaborador(any());
        verify(emailService).enviarPedidoValidacaoParaSeguranca(any(), any());
    }

    @Test
    void processarPreRegisto_comDocumento_deveAnexarDocumentoNaCredencial() {
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        MultipartFile ficheiro = new MockMultipartFile("doc", "ficha.pdf", "application/pdf", "conteudo".getBytes());

        Credencial resultado = registoService.processarPreRegisto(dtoValido(), List.of(ficheiro));

        assertThat(resultado.getDocumentos()).hasSize(1);
        assertThat(resultado.getDocumentos().get(0).getNomeOriginal()).isEqualTo("ficha.pdf");
    }

    @Test
    void processarPreRegisto_ficheiroVazioIgnorado_naoDeveAnexarDocumento() {
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        MultipartFile vazio = new MockMultipartFile("doc", "vazio.pdf", "application/pdf", new byte[0]);

        Credencial resultado = registoService.processarPreRegisto(dtoValido(), List.of(vazio));

        assertThat(resultado.getDocumentos()).isEmpty();
    }

    @Test
    void processarPreRegisto_maisDe3Ficheiros_deveLancarBusinessException() {
        List<MultipartFile> ficheiros = List.of(
            new MockMultipartFile("f1", "f1.pdf", "application/pdf", "a".getBytes()),
            new MockMultipartFile("f2", "f2.pdf", "application/pdf", "b".getBytes()),
            new MockMultipartFile("f3", "f3.pdf", "application/pdf", "c".getBytes()),
            new MockMultipartFile("f4", "f4.pdf", "application/pdf", "d".getBytes())
        );

        assertThatThrownBy(() -> registoService.processarPreRegisto(dtoValido(), ficheiros))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Máximo de 3 documentos permitido");

        verify(credencialRepository, never()).save(any());
    }

    // --- processarValidacao ---

    @Test
    void processarValidacao_tokenInvalido_deveLancarResourceNotFoundException() {
        when(credencialRepository.findByTokenValidacao("token-invalido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registoService.processarValidacao("token-invalido", "APROVAR"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void processarValidacao_credencialJaProcessada_deveRetornarHtmlSemAlterarEstado() {
        Credencial credencial = credencialComEstado(EstadoCredencial.APROVADA);
        when(credencialRepository.findByTokenValidacao("token-123")).thenReturn(Optional.of(credencial));

        String html = registoService.processarValidacao("token-123", "APROVAR");

        assertThat(html).contains("já foi");
        assertThat(credencial.getEstado()).isEqualTo(EstadoCredencial.APROVADA);
        verify(credencialRepository, never()).save(any());
        verify(emailService, never()).enviarNotificacaoSeguranca(any(), any());
    }

    @Test
    void processarValidacao_aprovar_deveAprovarCredencialNotificarSegurancaERetornarHtmlSucesso() {
        Credencial credencial = credencialComEstado(EstadoCredencial.AGUARDA_VALIDACAO);
        when(credencialRepository.findByTokenValidacao("token-ok")).thenReturn(Optional.of(credencial));
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String html = registoService.processarValidacao("token-ok", "APROVAR");

        assertThat(credencial.getEstado()).isEqualTo(EstadoCredencial.APROVADA);
        assertThat(html).contains("Aprovada");
        verify(credencialRepository).save(credencial);
        verify(emailService).enviarNotificacaoSeguranca(eq(credencial), any());
    }

    @Test
    void processarValidacao_rejeitar_deveRejeitarCredencialNotificarColaboradorSegurancaERetornarHtml() {
        Credencial credencial = credencialComEstado(EstadoCredencial.AGUARDA_VALIDACAO);
        when(credencialRepository.findByTokenValidacao("token-rej")).thenReturn(Optional.of(credencial));
        when(credencialRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String html = registoService.processarValidacao("token-rej", "REJEITAR");

        assertThat(credencial.getEstado()).isEqualTo(EstadoCredencial.REJEITADA);
        assertThat(html).contains("Rejeitada");
        verify(credencialRepository).save(credencial);
        verify(emailService).enviarRejeicaoColaborador(credencial);
        verify(emailService).enviarConfirmacaoRejeicaoSeguranca(eq(credencial), any());
    }

    @Test
    void processarValidacao_acaoInvalida_deveLancarBusinessException() {
        Credencial credencial = credencialComEstado(EstadoCredencial.AGUARDA_VALIDACAO);
        when(credencialRepository.findByTokenValidacao("token-ok")).thenReturn(Optional.of(credencial));

        assertThatThrownBy(() -> registoService.processarValidacao("token-ok", "CANCELAR"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("APROVAR ou REJEITAR");
    }

    // --- helpers ---

    private RegistoPublicoDTO dtoValido() {
        RegistoPublicoDTO dto = new RegistoPublicoDTO();
        dto.setNome("João Silva");
        dto.setEmail("joao@empresa.com");
        dto.setDataNascimento(LocalDate.of(1990, 5, 20));
        dto.setEmpresa("Empresa Teste");
        dto.setTipo(TipoColaborador.REPOSICAO);
        dto.setEmailResponsavel("resp@empresa.com");
        dto.setDataValidadeCredencial(LocalDate.now().plusMonths(6));
        dto.setDataValidadeFichaAptidao(LocalDate.now().plusMonths(6));
        dto.setNumApolice("AP001");
        dto.setDataValidadeSeguro(LocalDate.now().plusMonths(12));
        return dto;
    }

    private Credencial credencialComEstado(EstadoCredencial estado) {
        Credencial c = new Credencial();
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
