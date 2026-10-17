package br.com.callcenter.modulorh.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.callcenter.modulorh.domain.entity.AvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.entity.CicloAvaliacao;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.domain.enums.StatusAvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.enums.TipoAvaliacaoDesempenho;

@Repository
public interface AvaliacaoDesempenhoRepository extends JpaRepository<AvaliacaoDesempenho, UUID> {

       List<AvaliacaoDesempenho> findByFuncionarioAndStatusNot(Funcionario funcionario,
                     StatusAvaliacaoDesempenho status);

       List<AvaliacaoDesempenho> findByCicloAndFuncionario(CicloAvaliacao ciclo, Funcionario funcionario);

       List<AvaliacaoDesempenho> findByCicloAndFuncionarioAndTipo(CicloAvaliacao ciclo, Funcionario funcionario,
                     TipoAvaliacaoDesempenho tipo);

       List<AvaliacaoDesempenho> findByCicloAndTipo(CicloAvaliacao ciclo, TipoAvaliacaoDesempenho tipo);

       List<AvaliacaoDesempenho> findByAvaliadorAndStatus(Funcionario avaliador, StatusAvaliacaoDesempenho status);

       @Query("SELECT a FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId " +
                     "AND a.funcionario.id = :funcionarioId " +
                     "AND a.tipo = :tipo")
       Optional<AvaliacaoDesempenho> findByCicloFuncionarioETipo(
                     @Param("cicloId") UUID cicloId,
                     @Param("funcionarioId") UUID funcionarioId,
                     @Param("tipo") TipoAvaliacaoDesempenho tipo);

       @Query("SELECT a FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId " +
                     "AND a.funcionario.id = :funcionarioId " +
                     "AND a.status IN :statusList")
       List<AvaliacaoDesempenho> findByCicloFuncionarioEStatus(
                     @Param("cicloId") UUID cicloId,
                     @Param("funcionarioId") UUID funcionarioId,
                     @Param("statusList") List<StatusAvaliacaoDesempenho> statusList);

       @Query("SELECT a FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId " +
                     "AND a.status IN :statusList")
       Page<AvaliacaoDesempenho> findByCicloEStatus(
                     @Param("cicloId") UUID cicloId,
                     @Param("statusList") List<StatusAvaliacaoDesempenho> statusList,
                     Pageable pageable);

       @Query("SELECT a FROM AvaliacaoDesempenho a WHERE a.funcionario.id = :funcionarioId " +
                     "AND a.dataCriacao >= :dataInicio " +
                     "AND a.dataCriacao <= :dataFim")
       List<AvaliacaoDesempenho> findByFuncionarioEPeriodo(
                     @Param("funcionarioId") UUID funcionarioId,
                     @Param("dataInicio") LocalDateTime dataInicio,
                     @Param("dataFim") LocalDateTime dataFim);

       @Query("SELECT COUNT(a) FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId")
       Long countByCiclo(@Param("cicloId") UUID cicloId);

       @Query("SELECT a FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId")
       List<AvaliacaoDesempenho> findByCicloId(@Param("cicloId") UUID cicloId);

       @Query("SELECT COUNT(a) FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId " +
                     "AND a.status = :status")
       Long countByCicloEStatus(@Param("cicloId") UUID cicloId,
                     @Param("status") StatusAvaliacaoDesempenho status);

       @Query("SELECT AVG(a.notaFinal) FROM AvaliacaoDesempenho a WHERE a.ciclo.id = :cicloId " +
                     "AND a.notaFinal IS NOT NULL")
       Double calcularMediaNotasCiclo(@Param("cicloId") UUID cicloId);

       @Query("SELECT a.funcionario.departamento.nome, AVG(a.notaFinal) " +
                     "FROM AvaliacaoDesempenho a " +
                     "WHERE a.ciclo.id = :cicloId AND a.notaFinal IS NOT NULL " +
                     "GROUP BY a.funcionario.departamento.nome")
       List<Object[]> calcularMediaNotasPorDepartamento(@Param("cicloId") UUID cicloId);
}