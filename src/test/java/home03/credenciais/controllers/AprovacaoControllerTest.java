package home03.credenciais.controllers;

import home03.credenciais.config.ResourceServerConfig;
import home03.credenciais.services.FluxoAprovacaoService;
import home03.credenciais.services.exceptions.BusinessException;
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

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AprovacaoController.class)
@Import(ResourceServerConfig.class)
@ActiveProfiles("test")
class AprovacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FluxoAprovacaoService fluxoAprovacaoService;

    @MockBean
    private JwtDecoder jwtDecoder;

    // --- GET /aprovacao/{tokenId}/aprovar ---

    @Test
    void aprovar_tokenValido_deveRetornar200ComHtml() throws Exception {
        UUID tokenId = UUID.randomUUID();

        mockMvc.perform(get("/aprovacao/{tokenId}/aprovar", tokenId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Aprovada")));

        verify(fluxoAprovacaoService).aprovarPeloRM(tokenId);
    }

    @Test
    void aprovar_tokenExpirado_deveRetornar404() throws Exception {
        UUID tokenId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Token inválido ou expirado"))
            .when(fluxoAprovacaoService).aprovarPeloRM(tokenId);

        mockMvc.perform(get("/aprovacao/{tokenId}/aprovar", tokenId))
            .andExpect(status().isNotFound());
    }

    @Test
    void aprovar_tokenJaUtilizado_deveRetornar400() throws Exception {
        UUID tokenId = UUID.randomUUID();
        doThrow(new BusinessException("Token já utilizado"))
            .when(fluxoAprovacaoService).aprovarPeloRM(tokenId);

        mockMvc.perform(get("/aprovacao/{tokenId}/aprovar", tokenId))
            .andExpect(status().isBadRequest());
    }

    // --- GET /aprovacao/{tokenId}/rejeitar ---

    @Test
    void rejeitar_tokenValido_deveRetornar200ComHtml() throws Exception {
        UUID tokenId = UUID.randomUUID();

        mockMvc.perform(get("/aprovacao/{tokenId}/rejeitar", tokenId))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Rejeitada")));

        verify(fluxoAprovacaoService).rejeitarPeloRM(eq(tokenId), isNull());
    }

    @Test
    void rejeitar_comMotivo_deveChamarServicoComMotivo() throws Exception {
        UUID tokenId = UUID.randomUUID();

        mockMvc.perform(get("/aprovacao/{tokenId}/rejeitar", tokenId)
                .param("motivo", "Documentação incompleta"))
            .andExpect(status().isOk());

        verify(fluxoAprovacaoService).rejeitarPeloRM(tokenId, "Documentação incompleta");
    }

    @Test
    void rejeitar_tokenExpirado_deveRetornar404() throws Exception {
        UUID tokenId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Token inválido ou expirado"))
            .when(fluxoAprovacaoService).rejeitarPeloRM(eq(tokenId), any());

        mockMvc.perform(get("/aprovacao/{tokenId}/rejeitar", tokenId))
            .andExpect(status().isNotFound());
    }
}
