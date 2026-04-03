package home03.credenciais.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;

    @Column(unique = true)
    private String nif;

    private String morada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private ResponsavelMercado responsavel;

    public Empresa() {
    }

    public UUID getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public ResponsavelMercado getResponsavel() { return responsavel; }
    public void setResponsavel(ResponsavelMercado responsavel) { this.responsavel = responsavel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Empresa) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
