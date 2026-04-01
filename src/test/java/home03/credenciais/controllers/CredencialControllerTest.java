package home03.credenciais.controllers;

import home03.credenciais.config.ResourceServerConfig;
import home03.credenciais.dto.CredencialDTO;
import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;
import home03.credenciais.services.CredencialService;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CredencialController.class)
@Import(ResourceServerConfig.class)
@ActiveProfiles("test")
class CredencialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CredencialService credencialService;

    @MockBean
    private JwtDecoder jwtDecoder;

    // --- GET /api/credenciais ---

    @Test
    void listar_autenticado_deveRetornar200ComPagina() throws Exception {
        when(credencialService.listar(any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(credencialDTO())));

        mockMvc.perform(get("/api/credenciais")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECURITY"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].nome").value("João Silva"));
    }

    @Test
    void listar_semAutenticacao_deveRetornar401() throws Exception {
        mockMvc.perform(get("/api/credenciais"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void listar_comFiltroEstado_deveRetornar200() throws Exception {
        when(credencialService.listar(any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(credencialDTO())));

        mockMvc.perform(get("/api/credenciais").param("estado", "APROVADA")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isOk());
    }

    // --- GET /api/credenciais/{id} ---

    @Test
    void obterPorId_existente_deveRetornar200ComDados() throws Exception {
        CredencialDTO dto = credencialDTO();
        when(credencialService.obterPorId(dto.getId())).thenReturn(dto);

        mockMvc.perform(get("/api/credenciais/{id}", dto.getId())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_VIGILANTE"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("João Silva"))
            .andExpect(jsonPath("$.empresa").value("Empresa Teste"));
    }

    @Test
    void obterPorId_naoExiste_deveRetornar404() throws Exception {
        UUID id = UUID.randomUUID();
        when(credencialService.obterPorId(id))
            .thenThrow(new ResourceNotFoundException("Credencial não encontrada: " + id));

        mockMvc.perform(get("/api/credenciais/{id}", id)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECURITY"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void obterPorId_semAutenticacao_deveRetornar401() throws Exception {
        mockMvc.perform(get("/api/credenciais/{id}", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    // --- POST /api/credenciais/{id}/autorizar-entrada ---

    @Test
    void autorizarEntrada_credencialAprovada_deveRetornar200() throws Exception {
        CredencialDTO dto = credencialDTO();
        when(credencialService.autorizarEntrada(dto.getId())).thenReturn(dto);

        mockMvc.perform(post("/api/credenciais/{id}/autorizar-entrada", dto.getId())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECURITY"))))
            .andExpect(status().isOk());
    }

    @Test
    void autorizarEntrada_estadoInvalido_deveRetornar400() throws Exception {
        UUID id = UUID.randomUUID();
        when(credencialService.autorizarEntrada(id))
            .thenThrow(new BusinessException("Só é possível autorizar entrada de credenciais com estado APROVADA"));

        mockMvc.perform(post("/api/credenciais/{id}/autorizar-entrada", id)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_SECURITY"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Só é possível autorizar entrada de credenciais com estado APROVADA"));
    }

    @Test
    void autorizarEntrada_semAutenticacao_deveRetornar401() throws Exception {
        mockMvc.perform(post("/api/credenciais/{id}/autorizar-entrada", UUID.randomUUID()))
            .andExpect(status().isUnauthorized());
    }

    // --- helper ---

    private CredencialDTO credencialDTO() {
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
        c.setEstado(EstadoCredencial.APROVADA);
        return new CredencialDTO(c);
    }
}
