package home03.credenciais.entities;

import home03.credenciais.entities.enums.TipoToken;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_token_aprovacao")
public class TokenAprovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false)
    private Credencial credencial;

    @Enumerated(EnumType.STRING)
    private TipoToken tipo = TipoToken.APROVACAO_RM;

    private LocalDateTime dataExpiracao;

    private boolean utilizado = false;

    public TokenAprovacao() {
    }

    public TokenAprovacao(Credencial credencial, int horasValidade) {
        this.credencial = credencial;
        this.dataExpiracao = LocalDateTime.now().plusHours(horasValidade);
    }

    public UUID getId() { return id; }

    public Credencial getCredencial() { return credencial; }

    public TipoToken getTipo() { return tipo; }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }

    public boolean isUtilizado() { return utilizado; }
    public void marcarUtilizado() { this.utilizado = true; }

    public boolean isValido() {
        return !utilizado && LocalDateTime.now().isBefore(dataExpiracao);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((TokenAprovacao) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
