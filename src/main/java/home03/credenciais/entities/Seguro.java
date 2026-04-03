package home03.credenciais.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_seguro")
public class Seguro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false, unique = true)
    private Credencial credencial;

    @NotBlank
    private String apolice;

    @NotBlank
    private String seguradora;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "documento_id")
    private Documento documento;

    public Seguro() {
    }

    public UUID getId() { return id; }

    public Credencial getCredencial() { return credencial; }
    public void setCredencial(Credencial credencial) { this.credencial = credencial; }

    public String getApolice() { return apolice; }
    public void setApolice(String apolice) { this.apolice = apolice; }

    public String getSeguradora() { return seguradora; }
    public void setSeguradora(String seguradora) { this.seguradora = seguradora; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Documento getDocumento() { return documento; }
    public void setDocumento(Documento documento) { this.documento = documento; }

    public boolean isValido() {
        return dataFim != null && !LocalDate.now().isAfter(dataFim);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Seguro) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
