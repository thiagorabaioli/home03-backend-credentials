package home03.credenciais.entities;

import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;
import jakarta.persistence.*;

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

    @Column(unique = true, nullable = false)
    private String codigoInterno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private ColaboradorExterno colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    private TipoColaborador tipoColaborador;

    @Enumerated(EnumType.STRING)
    private EstadoCredencial estado = EstadoCredencial.PENDENTE;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private LocalDateTime dataRegisto = LocalDateTime.now();

    @OneToOne(mappedBy = "credencial", cascade = CascadeType.ALL, orphanRemoval = true)
    private Seguro seguro;

    @OneToOne(mappedBy = "credencial", cascade = CascadeType.ALL, orphanRemoval = true)
    private FichaAptidaoMedica fichaAptidaoMedica;

    @OneToOne(mappedBy = "credencial", cascade = CascadeType.ALL, orphanRemoval = true)
    private HorarioTrabalho horarioTrabalho;

    @OneToMany(mappedBy = "credencial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoEstado> historico = new ArrayList<>();

    public Credencial() {
    }

    public UUID getId() { return id; }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public ColaboradorExterno getColaborador() { return colaborador; }
    public void setColaborador(ColaboradorExterno colaborador) { this.colaborador = colaborador; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public TipoColaborador getTipoColaborador() { return tipoColaborador; }
    public void setTipoColaborador(TipoColaborador tipoColaborador) { this.tipoColaborador = tipoColaborador; }

    public EstadoCredencial getEstado() { return estado; }
    public void setEstado(EstadoCredencial estado) { this.estado = estado; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public LocalDateTime getDataRegisto() { return dataRegisto; }

    public Seguro getSeguro() { return seguro; }
    public void setSeguro(Seguro seguro) { this.seguro = seguro; }

    public FichaAptidaoMedica getFichaAptidaoMedica() { return fichaAptidaoMedica; }
    public void setFichaAptidaoMedica(FichaAptidaoMedica fichaAptidaoMedica) { this.fichaAptidaoMedica = fichaAptidaoMedica; }

    public HorarioTrabalho getHorarioTrabalho() { return horarioTrabalho; }
    public void setHorarioTrabalho(HorarioTrabalho horarioTrabalho) { this.horarioTrabalho = horarioTrabalho; }

    public List<HistoricoEstado> getHistorico() { return historico; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Credencial) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
