package home03.credenciais.dto;

import home03.credenciais.entities.enums.TipoColaborador;
import home03.credenciais.validators.MaiorDeIdade;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO recebido via multipart/form-data no endpoint público de pré-registo.
 * Os campos de ficheiro (documentos) são tratados separadamente no controller.
 */
public class RegistoPublicoDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotNull(message = "Data de nascimento é obrigatória")
    @MaiorDeIdade
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    @NotBlank(message = "Empresa é obrigatória")
    private String empresa;

    @NotNull(message = "Tipo de colaborador é obrigatório")
    private TipoColaborador tipo;

    @Email(message = "Email do responsável inválido")
    @NotBlank(message = "Email do responsável é obrigatório")
    private String emailResponsavel;

    @NotNull(message = "Validade da credencial é obrigatória")
    @FutureOrPresent(message = "A validade da credencial não pode ser uma data passada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataValidadeCredencial;

    @NotNull(message = "Validade da ficha de aptidão é obrigatória")
    @FutureOrPresent(message = "A validade da ficha de aptidão não pode ser uma data passada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataValidadeFichaAptidao;

    @NotBlank(message = "Nº de apólice é obrigatório")
    private String numApolice;

    @NotNull(message = "Validade do seguro é obrigatória")
    @FutureOrPresent(message = "A validade do seguro não pode ser uma data passada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataValidadeSeguro;

    public RegistoPublicoDTO() {
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public TipoColaborador getTipo() { return tipo; }
    public void setTipo(TipoColaborador tipo) { this.tipo = tipo; }

    public String getEmailResponsavel() { return emailResponsavel; }
    public void setEmailResponsavel(String emailResponsavel) { this.emailResponsavel = emailResponsavel; }

    public LocalDate getDataValidadeCredencial() { return dataValidadeCredencial; }
    public void setDataValidadeCredencial(LocalDate dataValidadeCredencial) { this.dataValidadeCredencial = dataValidadeCredencial; }

    public LocalDate getDataValidadeFichaAptidao() { return dataValidadeFichaAptidao; }
    public void setDataValidadeFichaAptidao(LocalDate dataValidadeFichaAptidao) { this.dataValidadeFichaAptidao = dataValidadeFichaAptidao; }

    public String getNumApolice() { return numApolice; }
    public void setNumApolice(String numApolice) { this.numApolice = numApolice; }

    public LocalDate getDataValidadeSeguro() { return dataValidadeSeguro; }
    public void setDataValidadeSeguro(LocalDate dataValidadeSeguro) { this.dataValidadeSeguro = dataValidadeSeguro; }
}
