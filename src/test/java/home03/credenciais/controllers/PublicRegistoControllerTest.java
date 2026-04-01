package home03.credenciais.controllers;

import home03.credenciais.config.ResourceServerConfig;
import home03.credenciais.services.RegistoService;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicRegistoController.class)
@Import(ResourceServerConfig.class)
@ActiveProfiles("test")
class PublicRegistoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistoService registoService;

    @MockBean
    private JwtDecoder jwtDecoder;

    // --- POST /api/public/registo ---

    @Test
    void submeterPreRegisto_dadosValidos_deveRetornar200() throws Exception {
        mockMvc.perform(multipart("/api/public/registo")
                .param("nome", "João Silva")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresa", "Empresa Teste")
                .param("tipo", "REPOSICAO")
                .param("emailResponsavel", "resp@empresa.com")
                .param("dataValidadeCredencial", "2026-12-31")
                .param("dataValidadeFichaAptidao", "2026-12-31")
                .param("numApolice", "AP001")
                .param("dataValidadeSeguro", "2027-12-31")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(registoService).processarPreRegisto(any(), any());
    }

    @Test
    void submeterPreRegisto_nomeFaltando_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/api/public/registo")
                // nome em falta (campo obrigatório)
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresa", "Empresa Teste")
                .param("tipo", "REPOSICAO")
                .param("emailResponsavel", "resp@empresa.com")
                .param("dataValidadeCredencial", "2026-12-31")
                .param("dataValidadeFichaAptidao", "2026-12-31")
                .param("numApolice", "AP001")
                .param("dataValidadeSeguro", "2027-12-31")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void submeterPreRegisto_emailInvalido_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/api/public/registo")
                .param("nome", "João Silva")
                .param("email", "email-invalido")
                .param("dataNascimento", "1990-05-20")
                .param("empresa", "Empresa Teste")
                .param("tipo", "REPOSICAO")
                .param("emailResponsavel", "resp@empresa.com")
                .param("dataValidadeCredencial", "2026-12-31")
                .param("dataValidadeFichaAptidao", "2026-12-31")
                .param("numApolice", "AP001")
                .param("dataValidadeSeguro", "2027-12-31")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void submeterPreRegisto_colaboradorMenorDeIdade_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/api/public/registo")
                .param("nome", "João Jovem")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "2015-01-01") // menos de 18 anos
                .param("empresa", "Empresa Teste")
                .param("tipo", "REPOSICAO")
                .param("emailResponsavel", "resp@empresa.com")
                .param("dataValidadeCredencial", "2026-12-31")
                .param("dataValidadeFichaAptidao", "2026-12-31")
                .param("numApolice", "AP001")
                .param("dataValidadeSeguro", "2027-12-31")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void submeterPreRegisto_dataValidadePassada_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/api/public/registo")
                .param("nome", "João Silva")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresa", "Empresa Teste")
                .param("tipo", "REPOSICAO")
                .param("emailResponsavel", "resp@empresa.com")
                .param("dataValidadeCredencial", "2020-01-01") // data passada
                .param("dataValidadeFichaAptidao", "2026-12-31")
                .param("numApolice", "AP001")
                .param("dataValidadeSeguro", "2027-12-31")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void submeterPreRegisto_dataValidadeSeguroPassada_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/api/public/registo")
                .param("nome", "João Silva")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresa", "Empresa Teste")
                .param("tipo", "REPOSICAO")
                .param("emailResponsavel", "resp@empresa.com")
                .param("dataValidadeCredencial", "2026-12-31")
                .param("dataValidadeFichaAptidao", "2026-12-31")
                .param("numApolice", "AP001")
                .param("dataValidadeSeguro", "2019-06-30") // data passada
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    // --- GET /api/public/validar ---

    @Test
    void validarCredencial_aprovar_deveRetornar200ComHtml() throws Exception {
        when(registoService.processarValidacao("token-ok", "APROVAR"))
            .thenReturn("<html><body>Credencial Aprovada</body></html>");

        mockMvc.perform(get("/api/public/validar")
                .param("token", "token-ok")
                .param("acao", "APROVAR"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Aprovada")));
    }

    @Test
    void validarCredencial_rejeitar_deveRetornar200ComHtml() throws Exception {
        when(registoService.processarValidacao("token-ok", "REJEITAR"))
            .thenReturn("<html><body>Credencial Rejeitada</body></html>");

        mockMvc.perform(get("/api/public/validar")
                .param("token", "token-ok")
                .param("acao", "REJEITAR"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    void validarCredencial_tokenInvalido_deveRetornar404() throws Exception {
        when(registoService.processarValidacao("token-invalido", "APROVAR"))
            .thenThrow(new ResourceNotFoundException("Token inválido ou expirado"));

        mockMvc.perform(get("/api/public/validar")
                .param("token", "token-invalido")
                .param("acao", "APROVAR"))
            .andExpect(status().isNotFound());
    }

    @Test
    void validarCredencial_acaoInvalida_deveRetornar400() throws Exception {
        when(registoService.processarValidacao("token-ok", "CANCELAR"))
            .thenThrow(new home03.credenciais.services.exceptions.BusinessException("Ação inválida. Use APROVAR ou REJEITAR."));

        mockMvc.perform(get("/api/public/validar")
                .param("token", "token-ok")
                .param("acao", "CANCELAR"))
            .andExpect(status().isBadRequest());
    }
}
