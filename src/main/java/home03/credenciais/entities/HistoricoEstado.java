package home03.credenciais.entities;

import home03.credenciais.entities.enums.EstadoCredencial;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_historico_estado")
public class HistoricoEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false)
    private Credencial credencial;

    @Enumerated(EnumType.STRING)
    private EstadoCredencial estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoCredencial estadoNovo;

    private String responsavel;

    private String observacoes;

    private LocalDateTime dataTransicao = LocalDateTime.now();

    public HistoricoEstado() {
    }

    public HistoricoEstado(Credencial credencial, EstadoCredencial estadoAnterior,
                           EstadoCredencial estadoNovo, String responsavel, String observacoes) {
        this.credencial = credencial;
        this.estadoAnterior = estadoAnterior;
        this.estadoNovo = estadoNovo;
        this.responsavel = responsavel;
        this.observacoes = observacoes;
    }

    public UUID getId() { return id; }

    public Credencial getCredencial() { return credencial; }

    public EstadoCredencial getEstadoAnterior() { return estadoAnterior; }

    public EstadoCredencial getEstadoNovo() { return estadoNovo; }

    public String getResponsavel() { return responsavel; }

    public String getObservacoes() { return observacoes; }

    public LocalDateTime getDataTransicao() { return dataTransicao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((HistoricoEstado) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
