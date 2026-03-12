package home03.credenciais.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id", nullable = false)
    private Credencial credencial;

    private String nomeOriginal;
    private String contentType;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] conteudo;

    private LocalDateTime dataUpload = LocalDateTime.now();

    public Documento() {
    }

    public Documento(String nomeOriginal, String contentType, byte[] conteudo) {
        this.nomeOriginal = nomeOriginal;
        this.contentType = contentType;
        this.conteudo = conteudo;
    }

    public Long getId() { return id; }

    public Credencial getCredencial() { return credencial; }
    public void setCredencial(Credencial credencial) { this.credencial = credencial; }

    public String getNomeOriginal() { return nomeOriginal; }
    public void setNomeOriginal(String nomeOriginal) { this.nomeOriginal = nomeOriginal; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getConteudo() { return conteudo; }
    public void setConteudo(byte[] conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataUpload() { return dataUpload; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Documento) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
