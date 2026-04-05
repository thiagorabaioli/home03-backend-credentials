package home03.credenciais.controllers;

import home03.credenciais.config.ResourceServerConfig;
import home03.credenciais.services.RegistoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

    private MockMultipartFile ficheiroTeste() {
        return new MockMultipartFile("documentos", "seguro.pdf",
                "application/pdf", "dummy-content".getBytes());
    }

    // --- POST /public/registo ---

    @Test
    void registar_dadosValidos_deveRetornar200() throws Exception {
        mockMvc.perform(multipart("/public/registo")
                .file(ficheiroTeste())
                .param("nome", "João Silva")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresaNome", "Empresa Teste")
                .param("tipoColaborador", "REPOSICAO")
                .param("dataInicio", LocalDate.now().toString())
                .param("dataFim", LocalDate.now().plusMonths(6).toString())
                .param("apolice", "AP001")
                .param("seguradora", "Seguros PT")
                .param("seguroDataInicio", LocalDate.now().toString())
                .param("seguroDataFim", LocalDate.now().plusMonths(12).toString())
                .param("diasSemana", "SEGUNDA")
                .param("horaEntrada", "09:00")
                .param("horaSaida", "17:00")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(registoService).registar(any(), any());
    }

    @Test
    void registar_nomeFaltando_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/public/registo")
                .file(ficheiroTeste())
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresaNome", "Empresa Teste")
                .param("tipoColaborador", "REPOSICAO")
                .param("dataInicio", LocalDate.now().toString())
                .param("dataFim", LocalDate.now().plusMonths(6).toString())
                .param("apolice", "AP001")
                .param("seguradora", "Seguros PT")
                .param("seguroDataInicio", LocalDate.now().toString())
                .param("seguroDataFim", LocalDate.now().plusMonths(12).toString())
                .param("diasSemana", "SEGUNDA")
                .param("horaEntrada", "09:00")
                .param("horaSaida", "17:00")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void registar_emailInvalido_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/public/registo")
                .file(ficheiroTeste())
                .param("nome", "João Silva")
                .param("email", "email-invalido")
                .param("dataNascimento", "1990-05-20")
                .param("empresaNome", "Empresa Teste")
                .param("tipoColaborador", "REPOSICAO")
                .param("dataInicio", LocalDate.now().toString())
                .param("dataFim", LocalDate.now().plusMonths(6).toString())
                .param("apolice", "AP001")
                .param("seguradora", "Seguros PT")
                .param("seguroDataInicio", LocalDate.now().toString())
                .param("seguroDataFim", LocalDate.now().plusMonths(12).toString())
                .param("diasSemana", "SEGUNDA")
                .param("horaEntrada", "09:00")
                .param("horaSaida", "17:00")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void registar_colaboradorMenorDeIdade_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/public/registo")
                .file(ficheiroTeste())
                .param("nome", "João Jovem")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "2015-01-01")
                .param("empresaNome", "Empresa Teste")
                .param("tipoColaborador", "REPOSICAO")
                .param("dataInicio", LocalDate.now().toString())
                .param("dataFim", LocalDate.now().plusMonths(6).toString())
                .param("apolice", "AP001")
                .param("seguradora", "Seguros PT")
                .param("seguroDataInicio", LocalDate.now().toString())
                .param("seguroDataFim", LocalDate.now().plusMonths(12).toString())
                .param("diasSemana", "SEGUNDA")
                .param("horaEntrada", "09:00")
                .param("horaSaida", "17:00")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void registar_dataFimPassada_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/public/registo")
                .file(ficheiroTeste())
                .param("nome", "João Silva")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresaNome", "Empresa Teste")
                .param("tipoColaborador", "REPOSICAO")
                .param("dataInicio", LocalDate.now().toString())
                .param("dataFim", "2020-01-01")
                .param("apolice", "AP001")
                .param("seguradora", "Seguros PT")
                .param("seguroDataInicio", LocalDate.now().toString())
                .param("seguroDataFim", LocalDate.now().plusMonths(12).toString())
                .param("diasSemana", "SEGUNDA")
                .param("horaEntrada", "09:00")
                .param("horaSaida", "17:00")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void registar_seguroDataFimPassada_deveRetornarErroValidacao() throws Exception {
        mockMvc.perform(multipart("/public/registo")
                .file(ficheiroTeste())
                .param("nome", "João Silva")
                .param("email", "joao@empresa.com")
                .param("dataNascimento", "1990-05-20")
                .param("empresaNome", "Empresa Teste")
                .param("tipoColaborador", "REPOSICAO")
                .param("dataInicio", LocalDate.now().toString())
                .param("dataFim", LocalDate.now().plusMonths(6).toString())
                .param("apolice", "AP001")
                .param("seguradora", "Seguros PT")
                .param("seguroDataInicio", LocalDate.now().toString())
                .param("seguroDataFim", "2019-06-30")
                .param("diasSemana", "SEGUNDA")
                .param("horaEntrada", "09:00")
                .param("horaSaida", "17:00")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().is4xxClientError());
    }
}
