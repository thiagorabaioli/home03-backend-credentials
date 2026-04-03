package home03.credenciais.entities;

import home03.credenciais.entities.enums.ResultadoFicha;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_ficha_aptidao_medica")
public class FichaAptidaoMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false, unique = true)
    private Credencial credencial;

    @NotNull
    private LocalDate dataEmissao;

    @NotNull
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ResultadoFicha resultado;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "documento_id")
    private Documento documento;

    public FichaAptidaoMedica() {
    }

    public UUID getId() { return id; }

    public Credencial getCredencial() { return credencial; }
    public void setCredencial(Credencial credencial) { this.credencial = credencial; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public ResultadoFicha getResultado() { return resultado; }
    public void setResultado(ResultadoFicha resultado) { this.resultado = resultado; }

    public Documento getDocumento() { return documento; }
    public void setDocumento(Documento documento) { this.documento = documento; }

    public boolean isValida() {
        return resultado == ResultadoFicha.APTO
                && dataValidade != null
                && !LocalDate.now().isAfter(dataValidade);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((FichaAptidaoMedica) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
