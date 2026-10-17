package br.com.callcenter.moduloanalytics.domain.repository;

import br.com.callcenter.moduloanalytics.domain.entity.AnalyticsIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalyticsIndicatorRepository extends JpaRepository<AnalyticsIndicator, UUID> {

    Optional<AnalyticsIndicator> findByIndicadorAndPeriodoAndDepartamentoId(String indicador, String periodo, UUID departamentoId);

    @Query("""
            SELECT a FROM AnalyticsIndicator a
            WHERE a.indicador = :indicador
            AND a.periodo BETWEEN :inicioPeriodo AND :fimPeriodo
            ORDER BY a.periodo DESC
            """)
    List<AnalyticsIndicator> findRecentIndicatorsByPeriod(
            @Param("indicador") String indicador,
            @Param("inicioPeriodo") String inicioPeriodo,
            @Param("fimPeriodo") String fimPeriodo);

    @Query("""
            SELECT a FROM AnalyticsIndicator a
            WHERE a.departamento IS NULL
            AND a.periodo = :periodo
            """)
    List<AnalyticsIndicator> findCompanyIndicatorsByPeriod(@Param("periodo") String periodo);
}
