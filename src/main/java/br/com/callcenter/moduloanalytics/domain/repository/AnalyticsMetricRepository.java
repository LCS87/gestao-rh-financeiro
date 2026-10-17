package br.com.callcenter.moduloanalytics.domain.repository;

import br.com.callcenter.moduloanalytics.domain.entity.AnalyticsMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AnalyticsMetricRepository extends JpaRepository<AnalyticsMetric, UUID> {

    List<AnalyticsMetric> findByMetricaAndDataBetween(String metrica, LocalDate inicio, LocalDate fim);

    List<AnalyticsMetric> findByDepartamentoIdAndDataBetween(UUID departamentoId, LocalDate inicio, LocalDate fim);
}
