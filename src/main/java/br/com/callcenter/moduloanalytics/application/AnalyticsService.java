package br.com.callcenter.moduloanalytics.application;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.callcenter.moduloanalytics.api.AnalyticsController.AnalyticsDashboardDto;
import br.com.callcenter.moduloanalytics.domain.entity.AnalyticsIndicator;
import br.com.callcenter.moduloanalytics.domain.entity.AnalyticsMetric;
import br.com.callcenter.moduloanalytics.domain.repository.AnalyticsIndicatorRepository;
import br.com.callcenter.moduloanalytics.domain.repository.AnalyticsMetricRepository;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.modulorh.infrastructure.repository.AfastamentoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.DemissaoRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FeriasRepository;
import br.com.callcenter.modulorh.infrastructure.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final FuncionarioRepository funcionarioRepository;
    private final FeriasRepository feriasRepository;
    private final AfastamentoRepository afastamentoRepository;
    private final DemissaoRepository demissaoRepository;
    private final AnalyticsIndicatorRepository analyticsIndicatorRepository;
    private final AnalyticsMetricRepository analyticsMetricRepository;

    // Atualiza todos os indicadores analytics
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?") // Toda madrugada às 3h
    public void calcularIndicadoresMensais() {
        String periodo = YearMonth.now().toString(); // YYYY-MM
        log.info("Calculando indicadores analytics para o período: {}", periodo);

        calcularRotatividade(periodo);
        calcularAbsenteismo(periodo);
        calcularCustoPorFuncionario(periodo);
        calcularTaxaFerias(periodo);
    }

    // Rotatividade = (Demissões no mês / Funcionários ativos no início do mês) *
    // 100
    private void calcularRotatividade(String periodo) {
        int mes = YearMonth.parse(periodo).getMonthValue();
        int ano = YearMonth.parse(periodo).getYear();

        // Funcionários ativos no início do mês (data_admissao <= último dia do mês
        // anterior)
        LocalDate primeiroDiaMes = YearMonth.parse(periodo).atDay(1);
        LocalDate ultimoDiaMesAnterior = primeiroDiaMes.minusDays(1);
        List<Funcionario> funcionariosAtivosInicioMes = funcionarioRepository
                .findByDataAdmissaoBeforeAndStatus(ultimoDiaMesAnterior, Funcionario.StatusFuncionario.ATIVO);

        int totalFuncionariosInicioMes = (int) funcionarioRepository.countByStatus(Funcionario.StatusFuncionario.ATIVO);
        if (totalFuncionariosInicioMes == 0) {
            salvarIndicator("rotatividade", 0.0, periodo, null);
            return;
        }

        // Demissões no mês
        LocalDate primeiroDiaMesAtual = primeiroDiaMes;
        LocalDate ultimoDiaMesAtual = YearMonth.parse(periodo).atEndOfMonth();
        List<br.com.callcenter.modulorh.domain.entity.Demissao> demissoesMes = demissaoRepository
                .findByDataDemissaoBetween(primeiroDiaMesAtual, ultimoDiaMesAtual);

        double rotatividade = (demissoesMes.size() * 1.0 / totalFuncionariosInicioMes) * 100;
        salvarIndicator("rotatividade", rotatividade, periodo, null);
        log.info("Rotatividade calculada: {:.2f}%", rotatividade);
    }

    // Absenteísmo = (Total de horas/faltas / (Funcionários ativos * dias úteis *
    // 8h)) * 100
    private void calcularAbsenteismo(String periodo) {
        int mes = YearMonth.parse(periodo).getMonthValue();
        int ano = YearMonth.parse(periodo).getYear();

        // Funcionários ativos no mês
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByStatus(Funcionario.StatusFuncionario.ATIVO);
        int totalFuncionarios = funcionariosAtivos.size();
        if (totalFuncionarios == 0) {
            salvarIndicator("absenteismo", 0.0, periodo, null);
            return;
        }

        // Contar faltas e afastamentos no mês
        LocalDate primeiroDia = YearMonth.parse(periodo).atDay(1);
        LocalDate ultimoDia = YearMonth.parse(periodo).atEndOfMonth();

        long totalAfastamentos = afastamentoRepository.countByDataInicioBetween(primeiroDia, ultimoDia);

        // Cálculo simplificado: assume 8h/dia e 22 dias úteis
        int diasUteis = 22; // Estimativa média
        double totalHorasPossiveis = totalFuncionarios * diasUteis * 8.0;
        double totalHorasFalta = totalAfastamentos * 8.0; // Assume 8h por afastamento/dia

        double absenteismo = (totalHorasFalta / totalHorasPossiveis) * 100;
        salvarIndicator("absenteismo", absenteismo, periodo, null);
        log.info("Absenteísmo calculado: {:.2f}%", absenteismo);
    }

    // Custo por Funcionário = (Folha de pagamento total do mês) / (Funcionários
    // ativos)
    private void calcularCustoPorFuncionario(String periodo) {
        // Para calcular o custo real, precisaria somar todos os contracheques do mês
        // Simplificação: usa folha de pagamento processada
        // Em produção, integrar com FolhaPagamentoService

        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByStatus(Funcionario.StatusFuncionario.ATIVO);
        int totalFuncionarios = funcionariosAtivos.size();
        if (totalFuncionarios == 0) {
            salvarIndicator("custo_por_funcionario", 0.0, periodo, null);
            return;
        }

        // Placeholder - em produção, somar salários líquidos da folha
        double custoTotalFolha = funcionariosAtivos.stream()
                .mapToDouble(f -> f.getSalarioBase() != null ? f.getSalarioBase() : 0)
                .sum();

        double custoPorFuncionario = custoTotalFolha / totalFuncionarios;
        salvarIndicator("custo_por_funcionario", custoPorFuncionario, periodo, null);
        log.info("Custo por funcionário calculado: R$ {:.2f}", custoPorFuncionario);
    }

    // Taxa de Férias = (Funcionários de férias / Funcionários ativos) * 100
    private void calcularTaxaFerias(String periodo) {
        List<Funcionario> funcionariosAtivos = funcionarioRepository.findByStatus(Funcionario.StatusFuncionario.ATIVO);
        int totalFuncionarios = funcionariosAtivos.size();
        if (totalFuncionarios == 0) {
            salvarIndicator("taxa_ferias", 0.0, periodo, null);
            return;
        }

        LocalDate primeiroDia = YearMonth.parse(periodo).atDay(1);
        LocalDate ultimoDia = YearMonth.parse(periodo).atEndOfMonth();

        // Funcionários em férias no período (férias iniciaram antes do fim do mês e
        // terminam após o início)
        long funcionariosEmFerias = feriasRepository.countByDataInicioBetween(primeiroDia, ultimoDia);

        double taxaFerias = (funcionariosEmFerias * 1.0 / totalFuncionarios) * 100;
        salvarIndicator("taxa_ferias", taxaFerias, periodo, null);
        log.info("Taxa de férias calculada: {:.2f}%", taxaFerias);
    }

    private void salvarIndicator(String indicador, double valor, String periodo,
            br.com.callcenter.modulorh.domain.entity.Departamento departamento) {
        analyticsIndicatorRepository
                .findByIndicadorAndPeriodoAndDepartamentoId(indicador, periodo,
                        departamento != null ? departamento.getId() : null)
                .ifPresentOrElse(
                        existing -> {
                            existing.setValor(valor);
                            analyticsIndicatorRepository.save(existing);
                        },
                        () -> {
                            AnalyticsIndicator indicator = AnalyticsIndicator.builder()
                                    .indicador(indicador)
                                    .valor(valor)
                                    .periodo(periodo)
                                    .departamento(departamento)
                                    .build();
                            analyticsIndicatorRepository.save(indicator);
                        });
    }

    // Contar métricas diárias
    @Transactional
    @Scheduled(cron = "0 0 4 * * ?") // Toda madrugada às 4h
    public void calcularMetricasDiarias() {
        LocalDate hoje = LocalDate.now();
        log.info("Calculando métricas diárias para: {}", hoje);

        contarAdmissoes(hoje);
        contarDemissoes(hoje);
        contarFerias(hoje);
        contarAfastamentos(hoje);
    }

    private void contarAdmissoes(LocalDate data) {
        // Contagem simplificada - em produção, contar por departamento
        long count = funcionarioRepository.countByDataAdmissao(data);
        salvarMetrica("admissoes", (int) count, data, null);
    }

    private void contarDemissoes(LocalDate data) {
        // Placeholder - em produção, contar demissões com data_demissao
        long count = demissaoRepository.countByDataDemissao(data);
        salvarMetrica("demissoes", (int) count, data, null);
    }

    private void contarFerias(LocalDate data) {
        long count = feriasRepository.countByDataInicio(data);
        salvarMetrica("ferias", (int) count, data, null);
    }

    private void contarAfastamentos(LocalDate data) {
        long count = afastamentoRepository.countByDataInicio(data);
        salvarMetrica("afastamentos", (int) count, data, null);
    }

    private void salvarMetrica(String metrica, int valor, LocalDate data,
            br.com.callcenter.modulorh.domain.entity.Departamento departamento) {
        AnalyticsMetric metric = AnalyticsMetric.builder()
                .metrica(metrica)
                .valor(valor)
                .data(data)
                .departamento(departamento)
                .build();
        analyticsMetricRepository.save(metric);
    }

    // Métodos públicos para os endpoints do controller
    public List<AnalyticsIndicator> getIndicatorsByPeriod(String periodo) {
        return analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getPeriodo().equals(periodo))
                .toList();
    }

    public List<AnalyticsIndicator> getIndicatorsByPeriodAndIndicator(String periodo, String indicador) {
        return analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getPeriodo().equals(periodo) && i.getIndicador().equals(indicador))
                .toList();
    }

    public List<AnalyticsIndicator> getRecentIndicators(String indicador, String periodoInicio, String periodoFim) {
        return analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getIndicador().equals(indicador) &&
                        i.getPeriodo().compareTo(periodoInicio) >= 0 &&
                        i.getPeriodo().compareTo(periodoFim) <= 0)
                .toList();
    }

    public List<AnalyticsMetric> getMetricsByPeriod(LocalDate inicio, LocalDate fim) {
        return analyticsMetricRepository.findAll().stream()
                .filter(m -> !m.getData().isBefore(inicio) && !m.getData().isAfter(fim))
                .toList();
    }

    public AnalyticsDashboardDto getDashboardSummary() {
        AnalyticsDashboardDto dto = new AnalyticsDashboardDto();

        // Carregar indicadores mais recentes do banco
        String periodoAtual = YearMonth.now().toString();
        String periodoAnterior = YearMonth.now().minusMonths(1).toString();

        // Carregar métricas recentes
        LocalDate hoje = LocalDate.now();
        LocalDate seteDiasAtras = hoje.minusDays(7);
        List<AnalyticsMetric> metricsRecentes = analyticsMetricRepository.findAll().stream()
                .filter(m -> !m.getData().isBefore(seteDiasAtras))
                .toList();

        // Calcular totais
        long totalFuncionarios = funcionarioRepository.countAll();
        int funcionariosAtivos = (int) funcionarioRepository.countByStatus(Funcionario.StatusFuncionario.ATIVO);
        int funcionariosInativos = (int) (totalFuncionarios - funcionariosAtivos);

        // Buscar indicadores mais recentes
        AnalyticsIndicator lastRotatividade = analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getIndicador().equals("rotatividade"))
                .findFirst()
                .orElse(null);

        AnalyticsIndicator lastAbsenteismo = analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getIndicador().equals("absenteismo"))
                .findFirst()
                .orElse(null);

        AnalyticsIndicator lastCusto = analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getIndicador().equals("custo_por_funcionario"))
                .findFirst()
                .orElse(null);

        AnalyticsIndicator lastFerias = analyticsIndicatorRepository.findAll().stream()
                .filter(i -> i.getIndicador().equals("taxa_ferias"))
                .findFirst()
                .orElse(null);

        dto.setRotatividade(lastRotatividade != null ? lastRotatividade.getValor() : 0.0);
        dto.setAbsenteismo(lastAbsenteismo != null ? lastAbsenteismo.getValor() : 0.0);
        dto.setCustoPorFuncionario(lastCusto != null ? lastCusto.getValor() : 0.0);
        dto.setTaxaFerias(lastFerias != null ? lastFerias.getValor() : 0.0);
        dto.setTotalFuncionarios((int) totalFuncionarios);
        dto.setFuncionariosAtivos(funcionariosAtivos);
        dto.setFuncionariosInativos(funcionariosInativos);

        return dto;
    }
}
