package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.CicloAvaliacao;
import br.com.callcenter.modulorh.domain.enums.StatusCicloAvaliacao;
import br.com.callcenter.modulorh.domain.enums.TipoCicloAvaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CicloAvaliacaoRepository extends JpaRepository<CicloAvaliacao, UUID> {

    List<CicloAvaliacao> findByAtivoTrue();

    Page<CicloAvaliacao> findByAtivoTrue(Pageable pageable);

    List<CicloAvaliacao> findByStatusAndAtivoTrue(StatusCicloAvaliacao status);

    List<CicloAvaliacao> findByTipoAndAnoReferencia(TipoCicloAvaliacao tipo, Integer anoReferencia);

    @Query("SELECT c FROM CicloAvaliacao c WHERE c.ativo = true " +
           "AND c.dataInicio <= :hoje AND c.dataFim >= :hoje")
    List<CicloAvaliacao> findCiclosEmAndamento(@Param("hoje") LocalDate hoje);

    @Query("SELECT c FROM CicloAvaliacao c WHERE c.ativo = true " +
           "AND ((c.dataInicioAutoavaliacao <= :hoje AND c.dataFimAutoavaliacao >= :hoje) OR " +
           "(c.dataInicioAvaliacaoGestor <= :hoje AND c.dataFimAvaliacaoGestor >= :hoje) OR " +
           "(c.dataInicioFeedback <= :hoje AND c.dataFimFeedback >= :hoje))")
    List<CicloAvaliacao> findCiclosComEtapasAtivas(@Param("hoje") LocalDate hoje);

    @Query("SELECT c FROM CicloAvaliacao c WHERE c.ativo = true " +
           "AND c.dataInicio <= :hoje AND c.dataFim >= :hoje " +
           "AND c.status = 'EM_ANDAMENTO'")
    Optional<CicloAvaliacao> findCicloAtivoEmAndamento(@Param("hoje") LocalDate hoje);

    @Query("SELECT COUNT(c) FROM CicloAvaliacao c WHERE c.ativo = true")
    Long countCiclosAtivos();

    @Query("SELECT c FROM CicloAvaliacao c WHERE c.ativo = true " +
           "AND c.anoReferencia = :ano " +
           "ORDER BY c.dataInicio ASC")
    List<CicloAvaliacao> findByAno(@Param("ano") Integer ano);

    boolean existsByNomeAndAnoReferenciaAndTipo(String nome, Integer anoReferencia, TipoCicloAvaliacao tipo);
}