package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.MetaPDI;
import br.com.callcenter.modulorh.domain.entity.PlanoDesenvolvimentoIndividual;
import br.com.callcenter.modulorh.domain.enums.StatusMetaPDI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MetaPDIRepository extends JpaRepository<MetaPDI, UUID> {

    List<MetaPDI> findByPlanoDesenvolvimento(PlanoDesenvolvimentoIndividual planoDesenvolvimento);

    @Query("SELECT m FROM MetaPDI m WHERE m.planoDesenvolvimento.id = :planoId " +
           "ORDER BY m.dataPrevistaConclusao ASC")
    List<MetaPDI> findByPlanoIdOrderByDataConclusao(@Param("planoId") UUID planoId);

    List<MetaPDI> findByStatus(StatusMetaPDI status);

    @Query("SELECT m FROM MetaPDI m WHERE m.planoDesenvolvimento.id = :planoId " +
           "AND m.status = :status")
    List<MetaPDI> findByPlanoIdEStatus(@Param("planoId") UUID planoId,
                                      @Param("status") StatusMetaPDI status);

    @Query("SELECT m FROM MetaPDI m WHERE m.dataPrevistaConclusao <= :data " +
           "AND m.status NOT IN ('CONCLUIDA', 'CANCELADA')")
    List<MetaPDI> findMetasAtrasadas(@Param("data") LocalDate data);

    @Query("SELECT m FROM MetaPDI m WHERE m.planoDesenvolvimento.avaliacao.funcionario.id = :funcionarioId " +
           "AND m.status NOT IN ('CONCLUIDA', 'CANCELADA')")
    List<MetaPDI> findMetasAtivasPorFuncionario(@Param("funcionarioId") UUID funcionarioId);

    @Query("SELECT COUNT(m) FROM MetaPDI m WHERE m.planoDesenvolvimento.id = :planoId")
    Long countByPlano(@Param("planoId") UUID planoId);

    @Query("SELECT COUNT(m) FROM MetaPDI m WHERE m.planoDesenvolvimento.id = :planoId " +
           "AND m.status = :status")
    Long countByPlanoEStatus(@Param("planoId") UUID planoId,
                            @Param("status") StatusMetaPDI status);

    @Query("SELECT m.status, COUNT(m) FROM MetaPDI m " +
           "WHERE m.planoDesenvolvimento.id = :planoId " +
           "GROUP BY m.status")
    List<Object[]> contarMetasPorStatus(@Param("planoId") UUID planoId);

    @Query("SELECT AVG(m.percentualConclusao) FROM MetaPDI m " +
           "WHERE m.planoDesenvolvimento.id = :planoId " +
           "AND m.status IN ('PLANEJADA', 'EM_ANDAMENTO')")
    Double calcularMediaConclusaoPorPlano(@Param("planoId") UUID planoId);
}