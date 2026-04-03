package home03.credenciais.dto;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class CredencialDTO {

    private UUID id;
    private String codigoInterno;
    private EstadoCredencial estado;
    private TipoColaborador tipoColaborador;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalDateTime dataRegisto;

    // Colaborador
    private UUID colaboradorId;
    private String colaboradorCodigoInterno;
    private String colaboradorNome;
    private String colaboradorEmail;

    // Empresa
    private UUID empresaId;
    private String empresaNome;
    private String responsavelEmail;

    // Seguro
    private String seguroApolice;
    private LocalDate seguroDataFim;
    private boolean seguroValido;

    // Ficha
    private LocalDate fichaDataValidade;
    private String fichaResultado;
    private boolean fichaValida;

    public CredencialDTO(Credencial c) {
        this.id = c.getId();
        this.codigoInterno = c.getCodigoInterno();
        this.estado = c.getEstado();
        this.tipoColaborador = c.getTipoColaborador();
        this.dataInicio = c.getDataInicio();
        this.dataFim = c.getDataFim();
        this.dataRegisto = c.getDataRegisto();

        if (c.getColaborador() != null) {
            this.colaboradorId = c.getColaborador().getId();
            this.colaboradorCodigoInterno = c.getColaborador().getCodigoInterno();
            this.colaboradorNome = c.getColaborador().getNome();
            this.colaboradorEmail = c.getColaborador().getEmail();
        }

        if (c.getEmpresa() != null) {
            this.empresaId = c.getEmpresa().getId();
            this.empresaNome = c.getEmpresa().getNome();
            if (c.getEmpresa().getResponsavel() != null) {
                this.responsavelEmail = c.getEmpresa().getResponsavel().getEmail();
            }
        }

        if (c.getSeguro() != null) {
            this.seguroApolice = c.getSeguro().getApolice();
            this.seguroDataFim = c.getSeguro().getDataFim();
            this.seguroValido = c.getSeguro().isValido();
        }

        if (c.getFichaAptidaoMedica() != null) {
            this.fichaDataValidade = c.getFichaAptidaoMedica().getDataValidade();
            this.fichaResultado = c.getFichaAptidaoMedica().getResultado() != null
                    ? c.getFichaAptidaoMedica().getResultado().name() : null;
            this.fichaValida = c.getFichaAptidaoMedica().isValida();
        }
    }

    public UUID getId() { return id; }
    public String getCodigoInterno() { return codigoInterno; }
    public EstadoCredencial getEstado() { return estado; }
    public TipoColaborador getTipoColaborador() { return tipoColaborador; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public UUID getColaboradorId() { return colaboradorId; }
    public String getColaboradorCodigoInterno() { return colaboradorCodigoInterno; }
    public String getColaboradorNome() { return colaboradorNome; }
    public String getColaboradorEmail() { return colaboradorEmail; }
    public UUID getEmpresaId() { return empresaId; }
    public String getEmpresaNome() { return empresaNome; }
    public String getResponsavelEmail() { return responsavelEmail; }
    public String getSeguroApolice() { return seguroApolice; }
    public LocalDate getSeguroDataFim() { return seguroDataFim; }
    public boolean isSeguroValido() { return seguroValido; }
    public LocalDate getFichaDataValidade() { return fichaDataValidade; }
    public String getFichaResultado() { return fichaResultado; }
    public boolean isFichaValida() { return fichaValida; }
}
