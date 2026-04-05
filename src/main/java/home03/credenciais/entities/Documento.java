package home03.credenciais.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nomeOriginal;

    private String contentType;

    private Long tamanho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credencial_id")
    private Credencial credencial;

    // Path relativo no filesystem do Pi: {uuid-credencial}/{nome-ficheiro}
    // Nunca guardar o conteúdo como LOB na base de dados
    @Column(nullable = false)
    private String caminhoFicheiro;

    private LocalDateTime dataUpload = LocalDateTime.now();

    public Documento() {
    }

    public Documento(String nomeOriginal, String contentType, Long tamanho, String caminhoFicheiro) {
        this.nomeOriginal = nomeOriginal;
        this.contentType = contentType;
        this.tamanho = tamanho;
        this.caminhoFicheiro = caminhoFicheiro;
    }

    public UUID getId() { return id; }

    public String getNomeOriginal() { return nomeOriginal; }
    public void setNomeOriginal(String nomeOriginal) { this.nomeOriginal = nomeOriginal; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) { this.tamanho = tamanho; }

    public String getCaminhoFicheiro() { return caminhoFicheiro; }
    public void setCaminhoFicheiro(String caminhoFicheiro) { this.caminhoFicheiro = caminhoFicheiro; }

    public Credencial getCredencial() { return credencial; }
    public void setCredencial(Credencial credencial) { this.credencial = credencial; }

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
