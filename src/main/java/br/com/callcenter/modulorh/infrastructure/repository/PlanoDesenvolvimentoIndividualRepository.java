package br.com.callcenter.modulorh.infrastructure.repository;

import br.com.callcenter.modulorh.domain.entity.AvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.domain.entity.PlanoDesenvolvimentoIndividual;
import br.com.callcenter.modulorh.domain.enums.StatusPDI;
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
public interface PlanoDesenvolvimentoIndividualRepository extends JpaRepository<PlanoDesenvolvimentoIndividual, UUID> {

    Optional<PlanoDesenvolvimentoIndividual> findByAvaliacao(AvaliacaoDesempenho avaliacao);

    @Query("SELECT p FROM PlanoDesenvolvimentoIndividual p WHERE p.avaliacao.funcionario.id = :funcionarioId")
    List<PlanoDesenvolvimentoIndividual> findByFuncionarioId(@Param("funcionarioId") UUID funcionarioId);

    List<PlanoDesenvolvimentoIndividual> findByStatus(StatusPDI status);

    @Query("SELECT p FROM PlanoDesenvolvimentoIndividual p WHERE p.avaliacao.funcionario.id = :funcionarioId " +
           "AND p.status IN :statusList")
    List<PlanoDesenvolvimentoIndividual> findByFuncionarioIdEStatus(
            @Param("funcionarioId") UUID funcionarioId,
            @Param("statusList") List<StatusPDI> statusList);

    @Query("SELECT p FROM PlanoDesenvolvimentoIndividual p WHERE p.dataTerminoPrevisto <= :data " +
           "AND p.status NOT IN ('CONCLUIDO', 'CANCELADO')")
    List<PlanoDesenvolvimentoIndividual> findPlanosAtrasados(@Param("data") LocalDate data);

    @Query("SELECT p FROM PlanoDesenvolvimentoIndividual p " +
           "WHERE p.avaliacao.funcionario.departamento.id = :departamentoId " +
           "AND p.status IN :statusList")
    Page<PlanoDesenvolvimentoIndividual> findByDepartamentoEStatus(
            @Param("departamentoId") UUID departamentoId,
            @Param("statusList") List<StatusPDI> statusList,
            Pageable pageable);

    @Query("SELECT p FROM PlanoDesenvolvimentoIndividual p WHERE p.avaliacao.funcionario.id = :funcionarioId " +
           "AND p.dataCriacao >= :dataInicio " +
           "AND p.dataCriacao <= :dataFim")
    List<PlanoDesenvolvimentoIndividual> findByFuncionarioEPeriodo(
            @Param("funcionarioId") UUID funcionarioId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COUNT(p) FROM PlanoDesenvolvimentoIndividual p " +
           "WHERE p.avaliacao.funcionario.departamento.id = :departamentoId")
    Long countByDepartamento(@Param("departamentoId") UUID departamentoId);

    @Query("SELECT COUNT(p) FROM PlanoDesenvolvimentoIndividual p " +
           "WHERE p.avaliacao.funcionario.departamento.id = :departamentoId " +
           "AND p.status = :status")
    Long countByDepartamentoEStatus(
            @Param("departamentoId") UUID departamentoId,
            @Param("status") StatusPDI status);

    @Query("SELECT AVG(p.percentualConclusao) FROM PlanoDesenvolvimentoIndividual p " +
           "WHERE p.avaliacao.funcionario.departamento.id = :departamentoId " +
           "AND p.status IN ('APROVADO', 'EM_ANDAMENTO')")
    Double calcularMediaConclusaoPorDepartamento(@Param("departamentoId") UUID departamentoId);

    @Query("SELECT p.avaliacao.funcionario.departamento.nome, AVG(p.percentualConclusao) " +
           "FROM PlanoDesenvolvimentoIndividual p " +
           "WHERE p.status IN ('APROVADO', 'EM_ANDAMENTO') " +
           "GROUP BY p.avaliacao.funcionario.departamento.nome")
    List<Object[]> calcularMediaConclusaoPorDepartamentoTodos();
}