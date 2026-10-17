package br.com.callcenter.moduloanalytics.api;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.callcenter.moduloanalytics.application.AnalyticsService;
import br.com.callcenter.moduloanalytics.domain.entity.AnalyticsIndicator;
import br.com.callcenter.moduloanalytics.domain.entity.AnalyticsMetric;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics e Dashboard", description = "Indicadores de RH e métricas do sistema")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/indicators")
    @Operation(summary = "Lista indicadores analytics por período")
    public ResponseEntity<List<AnalyticsIndicator>> getIndicators(
            @RequestParam String periodo,
            @RequestParam(required = false) String indicador) {
        List<AnalyticsIndicator> indicators;
        if (indicador != null && !indicador.isEmpty()) {
            indicators = analyticsService.getIndicatorsByPeriodAndIndicator(periodo, indicador);
        } else {
            indicators = analyticsService.getIndicatorsByPeriod(periodo);
        }
        return ResponseEntity.ok(indicators);
    }

    @GetMapping("/indicators/recent")
    @Operation(summary = "Lista indicadores recentes (últimos 6 meses)")
    public ResponseEntity<List<AnalyticsIndicator>> getRecentIndicators(
            @RequestParam String indicador) {
        String periodoInicio = YearMonth.now().minusMonths(5).toString();
        String periodoFim = YearMonth.now().toString();
        List<AnalyticsIndicator> recentIndicators = analyticsService.getRecentIndicators(
                indicador, periodoInicio, periodoFim);
        return ResponseEntity.ok(recentIndicators);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Lista métricas diárias por período")
    public ResponseEntity<List<AnalyticsMetric>> getMetrics(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        List<AnalyticsMetric> metrics = analyticsService.getMetricsByPeriod(inicio, fim);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Resumo do dashboard com indicadores principais")
    public ResponseEntity<AnalyticsDashboardDto> getDashboardSummary() {
        AnalyticsDashboardDto summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    // DTO para o dashboard
    public static class AnalyticsDashboardDto {
        private Double rotatividade;
        private Double absenteismo;
        private Double custoPorFuncionario;
        private Double taxaFerias;
        private Integer totalFuncionarios;
        private Integer funcionariosAtivos;
        private Integer funcionariosInativos;

        // Getters e setters
        public Double getRotatividade() {
            return rotatividade;
        }

        public void setRotatividade(Double rotatividade) {
            this.rotatividade = rotatividade;
        }

        public Double getAbsenteismo() {
            return absenteismo;
        }

        public void setAbsenteismo(Double absenteismo) {
            this.absenteismo = absenteismo;
        }

        public Double getCustoPorFuncionario() {
            return custoPorFuncionario;
        }

        public void setCustoPorFuncionario(Double custoPorFuncionario) {
            this.custoPorFuncionario = custoPorFuncionario;
        }

        public Double getTaxaFerias() {
            return taxaFerias;
        }

        public void setTaxaFerias(Double taxaFerias) {
            this.taxaFerias = taxaFerias;
        }

        public Integer getTotalFuncionarios() {
            return totalFuncionarios;
        }

        public void setTotalFuncionarios(Integer totalFuncionarios) {
            this.totalFuncionarios = totalFuncionarios;
        }

        public Integer getFuncionariosAtivos() {
            return funcionariosAtivos;
        }

        public void setFuncionariosAtivos(Integer funcionariosAtivos) {
            this.funcionariosAtivos = funcionariosAtivos;
        }

        public Integer getFuncionariosInativos() {
            return funcionariosInativos;
        }

        public void setFuncionariosInativos(Integer funcionariosInativos) {
            this.funcionariosInativos = funcionariosInativos;
        }
    }
}
