package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.AvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.entity.FeedbackAvaliacao;
import br.com.callcenter.modulorh.domain.enums.StatusFeedbackAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackAvaliacaoRepository extends JpaRepository<FeedbackAvaliacao, UUID> {

    Optional<FeedbackAvaliacao> findByAvaliacao(AvaliacaoDesempenho avaliacao);

    @Query("SELECT f FROM FeedbackAvaliacao f WHERE f.avaliacao.id = :avaliacaoId")
    Optional<FeedbackAvaliacao> findByAvaliacaoId(@Param("avaliacaoId") UUID avaliacaoId);

    List<FeedbackAvaliacao> findByStatus(StatusFeedbackAvaliacao status);

    @Query("SELECT f FROM FeedbackAvaliacao f WHERE f.avaliacao.funcionario.id = :funcionarioId " +
           "AND f.status = :status")
    List<FeedbackAvaliacao> findByFuncionarioIdEStatus(@Param("funcionarioId") UUID funcionarioId,
                                                      @Param("status") StatusFeedbackAvaliacao status);

    @Query("SELECT f FROM FeedbackAvaliacao f WHERE f.avaliacao.funcionario.id = :funcionarioId " +
           "AND f.dataAgendamento >= :dataInicio " +
           "AND f.dataAgendamento <= :dataFim")
    List<FeedbackAvaliacao> findByFuncionarioEAprendamento(
            @Param("funcionarioId") UUID funcionarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT f FROM FeedbackAvaliacao f WHERE f.dataAgendamento <= :dataLimite " +
           "AND f.status IN ('AGENDADO', 'AGUARDANDO_GESTOR', 'AGUARDANDO_FUNCIONARIO')")
    List<FeedbackAvaliacao> findFeedbacksProximos(@Param("dataLimite") LocalDateTime dataLimite);

    @Query("SELECT COUNT(f) FROM FeedbackAvaliacao f " +
           "WHERE f.avaliacao.ciclo.id = :cicloId")
    Long countByCiclo(@Param("cicloId") UUID cicloId);

    @Query("SELECT COUNT(f) FROM FeedbackAvaliacao f " +
           "WHERE f.avaliacao.ciclo.id = :cicloId " +
           "AND f.status = :status")
    Long countByCicloEStatus(@Param("cicloId") UUID cicloId,
                            @Param("status") StatusFeedbackAvaliacao status);

    @Query("SELECT f.status, COUNT(f) FROM FeedbackAvaliacao f " +
           "WHERE f.avaliacao.ciclo.id = :cicloId " +
           "GROUP BY f.status")
    List<Object[]> contarFeedbacksPorStatus(@Param("cicloId") UUID cicloId);

    @Query("SELECT f FROM FeedbackAvaliacao f " +
           "WHERE f.avaliacao.funcionario.departamento.id = :departamentoId " +
           "AND f.status IN :statusList")
    List<FeedbackAvaliacao> findByDepartamentoEStatus(
            @Param("departamentoId") UUID departamentoId,
            @Param("statusList") List<StatusFeedbackAvaliacao> statusList);
}