package br.com.callcenter.modulofinanceiro.infrastructure.repository;

import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, UUID> {

    List<FolhaPagamento> findByFuncionarioId(UUID funcionarioId);

    List<FolhaPagamento> findByAnoMes(String anoMes);

    List<FolhaPagamento> findByStatus(FolhaPagamento.StatusFolha status);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.funcionarioId = :funcionarioId AND f.anoMes = :anoMes")
    FolhaPagamento findByFuncionarioEAnoMes(@Param("funcionarioId") UUID funcionarioId, @Param("anoMes") String anoMes);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.anoMes BETWEEN :inicio AND :fim ORDER BY f.anoMes DESC, f.funcionarioId")
    List<FolhaPagamento> findByPeriodo(@Param("inicio") String inicio, @Param("fim") String fim);

    @Query("SELECT SUM(f.salarioLiquido) FROM FolhaPagamento f WHERE f.anoMes = :anoMes AND f.status = 'PAGA'")
    Double sumTotalPagoNoMes(@Param("anoMes") String anoMes);

    @Query("SELECT COUNT(f) FROM FolhaPagamento f WHERE f.anoMes = :anoMes AND f.status = 'PROCESSANDO'")
    Long countProcessandoNoMes(@Param("anoMes") String anoMes);

    boolean existsByFuncionarioIdAndAnoMes(UUID funcionarioId, String anoMes);

    @Query("SELECT f FROM FolhaPagamento f WHERE f.status = 'PROCESSADA' AND f.dataPagamento IS NULL ORDER BY f.anoMes")
    List<FolhaPagamento> findProcessadasNaoPagas();
}
