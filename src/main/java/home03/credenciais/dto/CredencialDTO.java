package home03.credenciais.dto;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class CredencialDTO {

    private UUID id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String empresa;
    private TipoColaborador tipo;
    private String emailResponsavel;
    private LocalDate dataValidadeCredencial;
    private LocalDate dataValidadeFichaAptidao;
    private String numApolice;
    private LocalDate dataValidadeSeguro;
    private EstadoCredencial estado;
    private LocalDateTime dataCriacao;
    private int totalDocumentos;

    public CredencialDTO(Credencial c) {
        this.id = c.getId();
        this.nome = c.getNome();
        this.email = c.getEmail();
        this.dataNascimento = c.getDataNascimento();
        this.empresa = c.getEmpresa();
        this.tipo = c.getTipo();
        this.emailResponsavel = c.getEmailResponsavel();
        this.dataValidadeCredencial = c.getDataValidadeCredencial();
        this.dataValidadeFichaAptidao = c.getDataValidadeFichaAptidao();
        this.numApolice = c.getNumApolice();
        this.dataValidadeSeguro = c.getDataValidadeSeguro();
        this.estado = c.getEstado();
        this.dataCriacao = c.getDataCriacao();
        this.totalDocumentos = c.getDocumentos().size();
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getEmpresa() { return empresa; }
    public TipoColaborador getTipo() { return tipo; }
    public String getEmailResponsavel() { return emailResponsavel; }
    public LocalDate getDataValidadeCredencial() { return dataValidadeCredencial; }
    public LocalDate getDataValidadeFichaAptidao() { return dataValidadeFichaAptidao; }
    public String getNumApolice() { return numApolice; }
    public LocalDate getDataValidadeSeguro() { return dataValidadeSeguro; }
    public EstadoCredencial getEstado() { return estado; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public int getTotalDocumentos() { return totalDocumentos; }
}
