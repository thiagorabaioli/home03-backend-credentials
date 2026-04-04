package home03.credenciais.repositories;

import home03.credenciais.entities.Credencial;
import home03.credenciais.entities.enums.EstadoCredencial;
import home03.credenciais.entities.enums.TipoColaborador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, UUID>,
        JpaSpecificationExecutor<Credencial> {

    Page<Credencial> findByEstado(EstadoCredencial estado, Pageable pageable);

    boolean existsByColaboradorCodigoInternoAndEmpresaIdAndTipoColaboradorAndEstadoIn(
            String codigoInterno, UUID empresaId, TipoColaborador tipoColaborador,
            List<EstadoCredencial> estadosAtivos);

    /** Credenciais com dataFim anterior a hoje e estado ainda activo */
    @Query("SELECT c FROM Credencial c WHERE c.dataFim < :hoje AND c.estado NOT IN :estadosFinais")
    List<Credencial> findExpiradas(
            @Param("hoje") LocalDate hoje,
            @Param("estadosFinais") List<EstadoCredencial> estadosFinais);

    /** Credenciais cujo seguro expira nos próximos dias */
    @Query("SELECT c FROM Credencial c WHERE c.seguro.dataFim BETWEEN :inicio AND :fim AND c.estado NOT IN :estadosFinais")
    List<Credencial> findComSeguroAExpirar(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("estadosFinais") List<EstadoCredencial> estadosFinais);

    /** Credenciais cuja FAM expira nos próximos dias */
    @Query("SELECT c FROM Credencial c WHERE c.fichaAptidaoMedica.dataValidade BETWEEN :inicio AND :fim AND c.estado NOT IN :estadosFinais")
    List<Credencial> findComFamAExpirar(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("estadosFinais") List<EstadoCredencial> estadosFinais);
}
