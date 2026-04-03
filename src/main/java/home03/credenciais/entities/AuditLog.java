package home03.credenciais.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registo de auditoria independente — sem FK para Credencial.
 * Sobrevive à eliminação da credencial.
 */
@Entity
@Table(name = "tb_audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Código da credencial afectada (ex: CD-2026-0001) — String, não FK */
    @Column(nullable = false)
    private String codigoCredencial;

    /** Código do colaborador (ex: CE-2026-0001) — pode ser null se ainda não existe */
    private String codigoColaborador;

    /**
     * Tipo de acção: TRANSICAO_ESTADO, REGISTO, ELIMINACAO, EDICAO
     */
    @Column(nullable = false)
    private String acao;

    /** Descrição do que aconteceu */
    @Column(length = 1000)
    private String detalhe;

    /** Email do utilizador ou "SISTEMA" */
    @Column(nullable = false)
    private String utilizador;

    private LocalDateTime dataAcao = LocalDateTime.now();

    public AuditLog() {
    }

    public UUID getId() { return id; }

    public String getCodigoCredencial() { return codigoCredencial; }
    public void setCodigoCredencial(String codigoCredencial) { this.codigoCredencial = codigoCredencial; }

    public String getCodigoColaborador() { return codigoColaborador; }
    public void setCodigoColaborador(String codigoColaborador) { this.codigoColaborador = codigoColaborador; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getDetalhe() { return detalhe; }
    public void setDetalhe(String detalhe) { this.detalhe = detalhe; }

    public String getUtilizador() { return utilizador; }
    public void setUtilizador(String utilizador) { this.utilizador = utilizador; }

    public LocalDateTime getDataAcao() { return dataAcao; }
}
