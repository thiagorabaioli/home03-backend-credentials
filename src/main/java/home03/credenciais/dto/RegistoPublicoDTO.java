package home03.credenciais.dto;

import home03.credenciais.entities.enums.DiaSemana;
import home03.credenciais.entities.enums.ResultadoFicha;
import home03.credenciais.entities.enums.TipoColaborador;
import home03.credenciais.validators.MaiorDeIdade;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO recebido via multipart/form-data no endpoint público de registo.
 * Os ficheiros (documentoSeguro, documentoFicha) são tratados separadamente no controller.
 */
public class RegistoPublicoDTO {

    // --- Colaborador Externo ---

    /**
     * Código interno (CE-YYYY-NNNN) para CEs que já se registaram anteriormente.
     * Opcional: se omitido, um novo colaborador é criado e o código enviado por email.
     */
    private String codigoInterno;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    private String telefone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @MaiorDeIdade
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    // --- Credencial ---

    @NotNull(message = "Empresa é obrigatória")
    private UUID empresaId;

    @NotNull(message = "Tipo de colaborador é obrigatório")
    private TipoColaborador tipoColaborador;

    @NotNull(message = "Data de início é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    @FutureOrPresent(message = "A validade da credencial não pode ser uma data passada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFim;

    // --- Seguro ---

    @NotBlank(message = "Nº de apólice é obrigatório")
    private String apolice;

    @NotBlank(message = "Seguradora é obrigatória")
    private String seguradora;

    @NotNull(message = "Data de início do seguro é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate seguroDataInicio;

    @NotNull(message = "Validade do seguro é obrigatória")
    @FutureOrPresent(message = "A validade do seguro não pode ser uma data passada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate seguroDataFim;

    // --- Ficha de Aptidão Médica ---

    @NotNull(message = "Data de emissão da ficha é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fichaDataEmissao;

    @NotNull(message = "Data de validade da ficha é obrigatória")
    @FutureOrPresent(message = "A validade da ficha não pode ser uma data passada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fichaDataValidade;

    @NotNull(message = "Resultado da ficha é obrigatório")
    private ResultadoFicha fichaResultado;

    // --- Horário de Trabalho ---

    @NotEmpty(message = "Pelo menos um dia de trabalho é obrigatório")
    private Set<DiaSemana> diasSemana;

    @NotNull(message = "Hora de entrada é obrigatória")
    private LocalTime horaEntrada;

    @NotNull(message = "Hora de saída é obrigatória")
    private LocalTime horaSaida;

    public RegistoPublicoDTO() {
    }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public UUID getEmpresaId() { return empresaId; }
    public void setEmpresaId(UUID empresaId) { this.empresaId = empresaId; }

    public TipoColaborador getTipoColaborador() { return tipoColaborador; }
    public void setTipoColaborador(TipoColaborador tipoColaborador) { this.tipoColaborador = tipoColaborador; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getApolice() { return apolice; }
    public void setApolice(String apolice) { this.apolice = apolice; }

    public String getSeguradora() { return seguradora; }
    public void setSeguradora(String seguradora) { this.seguradora = seguradora; }

    public LocalDate getSeguroDataInicio() { return seguroDataInicio; }
    public void setSeguroDataInicio(LocalDate seguroDataInicio) { this.seguroDataInicio = seguroDataInicio; }

    public LocalDate getSeguroDataFim() { return seguroDataFim; }
    public void setSeguroDataFim(LocalDate seguroDataFim) { this.seguroDataFim = seguroDataFim; }

    public LocalDate getFichaDataEmissao() { return fichaDataEmissao; }
    public void setFichaDataEmissao(LocalDate fichaDataEmissao) { this.fichaDataEmissao = fichaDataEmissao; }

    public LocalDate getFichaDataValidade() { return fichaDataValidade; }
    public void setFichaDataValidade(LocalDate fichaDataValidade) { this.fichaDataValidade = fichaDataValidade; }

    public ResultadoFicha getFichaResultado() { return fichaResultado; }
    public void setFichaResultado(ResultadoFicha fichaResultado) { this.fichaResultado = fichaResultado; }

    public Set<DiaSemana> getDiasSemana() { return diasSemana; }
    public void setDiasSemana(Set<DiaSemana> diasSemana) { this.diasSemana = diasSemana; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalTime getHoraSaida() { return horaSaida; }
    public void setHoraSaida(LocalTime horaSaida) { this.horaSaida = horaSaida; }
}
