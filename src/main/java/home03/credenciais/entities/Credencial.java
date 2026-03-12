package home03.credenciais.entities;

import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_credencial")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private LocalDate dataNascimento;

    @NotBlank
    private String empresa;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoColaborador tipo;

    @Email
    @NotBlank
    private String emailResponsavel;

    @NotNull
    private LocalDate dataValidadeCredencial;

    @NotNull
    private LocalDate dataValidadeFichaAptidao;

    @NotBlank
    private String numApolice;

    @NotNull
    private LocalDate dataValidadeSeguro;

    @Enumerated(EnumType.STRING)
    private EstadoCredencial estado = EstadoCredencial.PENDENTE;

    @Column(unique = true)
    private String tokenValidacao = UUID.randomUUID().toString();

    private LocalDateTime dataCriacao = LocalDateTime.now();

    @OneToMany(mappedBy = "credencial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos = new ArrayList<>();

    public Credencial() {
    }

    public UUID getId() { return id; }

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

    public EstadoCredencial getEstado() { return estado; }
    public void setEstado(EstadoCredencial estado) { this.estado = estado; }

    public String getTokenValidacao() { return tokenValidacao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public List<Documento> getDocumentos() { return documentos; }
    public void addDocumento(Documento documento) {
        documento.setCredencial(this);
        this.documentos.add(documento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Credencial) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
