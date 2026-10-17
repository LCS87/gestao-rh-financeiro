package br.com.callcenter.modulo_comissoes.domain.repository;

import br.com.callcenter.modulo_comissoes.domain.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetaRepository extends JpaRepository<Meta, UUID> {

    List<Meta> findByFuncionarioId(UUID funcionarioId);

    List<Meta> findByFuncionarioIdAndPeriodoReferencia(UUID funcionarioId, String periodoReferencia);

    List<Meta>findByFuncionarioIdAndStatus(UUID funcionarioId, String status);

    @Query("SELECT m FROM Meta m WHERE m.status = 'ATIVA' AND m.periodoReferencia = :periodo")
    List<Meta> findAtivasNoPeriodo(@Param("periodo") String periodo);

    Optional<Meta> findByFuncionarioIdAndTipoMetaAndPeriodoReferencia(
            UUID funcionarioId, String tipoMeta, String periodoReferencia);
}
