package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.AvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.entity.QuestaoAvaliacao;
import br.com.callcenter.modulorh.domain.entity.RespostaAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RespostaAvaliacaoRepository extends JpaRepository<RespostaAvaliacao, UUID> {

    List<RespostaAvaliacao> findByAvaliacao(AvaliacaoDesempenho avaliacao);

    @Query("SELECT r FROM RespostaAvaliacao r WHERE r.avaliacao.id = :avaliacaoId " +
           "ORDER BY r.questao.ordem ASC")
    List<RespostaAvaliacao> findByAvaliacaoIdOrderByQuestaoOrdem(@Param("avaliacaoId") UUID avaliacaoId);

    Optional<RespostaAvaliacao> findByAvaliacaoAndQuestao(AvaliacaoDesempenho avaliacao, QuestaoAvaliacao questao);

    @Query("SELECT r FROM RespostaAvaliacao r WHERE r.avaliacao.id = :avaliacaoId " +
           "AND r.questao.id = :questaoId")
    Optional<RespostaAvaliacao> findByAvaliacaoIdAndQuestaoId(
            @Param("avaliacaoId") UUID avaliacaoId,
            @Param("questaoId") UUID questaoId);

    @Query("SELECT COUNT(r) FROM RespostaAvaliacao r WHERE r.avaliacao.id = :avaliacaoId")
    Long countByAvaliacao(@Param("avaliacaoId") UUID avaliacaoId);

    @Query("SELECT COUNT(r) FROM RespostaAvaliacao r WHERE r.avaliacao.id = :avaliacaoId " +
           "AND r.resposta IS NOT NULL AND r.resposta != ''")
    Long countRespostasPreenchidasByAvaliacao(@Param("avaliacaoId") UUID avaliacaoId);

    @Query("SELECT r.nota, COUNT(r) FROM RespostaAvaliacao r " +
           "WHERE r.avaliacao.ciclo.id = :cicloId AND r.nota IS NOT NULL " +
           "GROUP BY r.nota " +
           "ORDER BY r.nota ASC")
    List<Object[]> contarDistribuicaoNotasPorCiclo(@Param("cicloId") UUID cicloId);

    @Query("SELECT AVG(r.nota) FROM RespostaAvaliacao r " +
           "WHERE r.avaliacao.ciclo.id = :cicloId AND r.questao.id = :questaoId " +
           "AND r.nota IS NOT NULL")
    Double calcularMediaNotaPorQuestaoECiclo(
            @Param("questaoId") UUID questaoId,
            @Param("cicloId") UUID cicloId);

    void deleteByAvaliacaoId(UUID avaliacaoId);
}