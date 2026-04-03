package home03.credenciais.entities;

import home03.credenciais.entities.enums.TipoNotificacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_notificacao_email")
public class NotificacaoEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false)
    private Credencial credencial;

    private String destinatario;

    private String assunto;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    private LocalDateTime dataEnvio = LocalDateTime.now();

    private boolean sucesso;

    private String erro;

    public NotificacaoEmail() {
    }

    public NotificacaoEmail(Credencial credencial, String destinatario,
                            String assunto, TipoNotificacao tipo, boolean sucesso) {
        this.credencial = credencial;
        this.destinatario = destinatario;
        this.assunto = assunto;
        this.tipo = tipo;
        this.sucesso = sucesso;
    }

    public UUID getId() { return id; }

    public Credencial getCredencial() { return credencial; }

    public String getDestinatario() { return destinatario; }

    public String getAssunto() { return assunto; }

    public TipoNotificacao getTipo() { return tipo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }

    public boolean isSucesso() { return sucesso; }

    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((NotificacaoEmail) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
