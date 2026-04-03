package home03.credenciais.entities;

import home03.credenciais.entities.enums.DiaSemana;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_horario_trabalho")
public class HorarioTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false, unique = true)
    private Credencial credencial;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "tb_horario_dias", joinColumns = @JoinColumn(name = "horario_id"))
    @Column(name = "dia")
    private Set<DiaSemana> diasSemana = EnumSet.noneOf(DiaSemana.class);

    @NotNull
    private LocalTime horaEntrada;

    @NotNull
    private LocalTime horaSaida;

    public HorarioTrabalho() {
    }

    public UUID getId() { return id; }

    public Credencial getCredencial() { return credencial; }
    public void setCredencial(Credencial credencial) { this.credencial = credencial; }

    public Set<DiaSemana> getDiasSemana() { return diasSemana; }
    public void setDiasSemana(Set<DiaSemana> diasSemana) { this.diasSemana = diasSemana; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalTime getHoraSaida() { return horaSaida; }
    public void setHoraSaida(LocalTime horaSaida) { this.horaSaida = horaSaida; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((HorarioTrabalho) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
