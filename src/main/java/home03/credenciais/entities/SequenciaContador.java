package home03.credenciais.entities;

import home03.credenciais.entities.enums.TipoSequencia;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_sequencia_contador",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tipo", "ano"}))
public class SequenciaContador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSequencia tipo;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private int ultimoNumero = 0;

    public SequenciaContador() {
    }

    public SequenciaContador(TipoSequencia tipo, int ano) {
        this.tipo = tipo;
        this.ano = ano;
        this.ultimoNumero = 0;
    }

    public UUID getId() { return id; }

    public TipoSequencia getTipo() { return tipo; }

    public int getAno() { return ano; }

    public int getUltimoNumero() { return ultimoNumero; }
    public void setUltimoNumero(int ultimoNumero) { this.ultimoNumero = ultimoNumero; }
}
