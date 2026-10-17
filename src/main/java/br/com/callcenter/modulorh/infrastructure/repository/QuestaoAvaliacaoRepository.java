package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.FormularioAvaliacao;
import br.com.callcenter.modulorh.domain.entity.QuestaoAvaliacao;
import br.com.callcenter.modulorh.domain.enums.TipoQuestaoAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestaoAvaliacaoRepository extends JpaRepository<QuestaoAvaliacao, UUID> {

    List<QuestaoAvaliacao> findByFormularioOrderByOrdemAsc(FormularioAvaliacao formulario);

    @Query("SELECT q FROM QuestaoAvaliacao q WHERE q.formulario.id = :formularioId " +
           "ORDER BY q.ordem ASC")
    List<QuestaoAvaliacao> findByFormularioIdOrderByOrdem(@Param("formularioId") UUID formularioId);

    List<QuestaoAvaliacao> findByFormularioAndTipo(FormularioAvaliacao formulario, TipoQuestaoAvaliacao tipo);

    @Query("SELECT q FROM QuestaoAvaliacao q WHERE q.formulario.id = :formularioId " +
           "AND q.obrigatoria = true " +
           "ORDER BY q.ordem ASC")
    List<QuestaoAvaliacao> findQuestoesObrigatoriasByFormulario(@Param("formularioId") UUID formularioId);

    @Query("SELECT COUNT(q) FROM QuestaoAvaliacao q WHERE q.formulario.id = :formularioId")
    Long countByFormulario(@Param("formularioId") UUID formularioId);

    @Query("SELECT q FROM QuestaoAvaliacao q WHERE q.formulario.id = :formularioId " +
           "AND q.tipo IN :tipos " +
           "ORDER BY q.ordem ASC")
    List<QuestaoAvaliacao> findByFormularioETipos(
            @Param("formularioId") UUID formularioId,
            @Param("tipos") List<TipoQuestaoAvaliacao> tipos);

    void deleteByFormularioId(UUID formularioId);
}