package br.com.callcenter.modulorelatorios.application;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.callcenter.modulofinanceiro.domain.entity.ContaPagar;
import br.com.callcenter.modulofinanceiro.domain.entity.ContaReceber;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.ContaPagarRepository;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.ContaReceberRepository;
import br.com.callcenter.modulofinanceiro.infrastructure.repository.FolhaPagamentoRepository;
import br.com.callcenter.modulorelatorios.domain.entity.DRE;
import br.com.callcenter.modulorelatorios.domain.entity.FluxoCaixa;
import br.com.callcenter.modulorelatorios.domain.repository.DRERepository;
import br.com.callcenter.modulorelatorios.domain.repository.FluxoCaixaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatoriosService {

    private final ContaPagarRepository contaPagarRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final FolhaPagamentoRepository folhaPagamentoRepository;
    private final DRERepository dreRepository;
    private final FluxoCaixaRepository fluxoCaixaRepository;

    // Gera DRE mensalmente
    @Transactional
    @Scheduled(cron = "0 30 4 * * ?") // Toda madrugada às 4:30h
    public void gerarDREMensal() {
        String periodo = YearMonth.now().toString(); // YYYY-MM
        log.info("Gerando DRE para o período: {}", periodo);

        DRE dre = calcularDRE(periodo);
        dreRepository.findByPeriodo(periodo)
                .ifPresentOrElse(
                        existing -> {
                            dre.setId(existing.getId());
                            dreRepository.save(dre);
                        },
                        () -> dreRepository.save(dre));

        log.info("DRE gerada com sucesso para o período: {}", periodo);
    }

    // Gera Fluxo de Caixa diário
    @Transactional
    @Scheduled(cron = "0 0 5 * * ?") // Toda madrugada às 5h
    public void gerarFluxoCaixaDiario() {
        LocalDate hoje = LocalDate.now();
        log.info("Gerando fluxo de caixa para a data: {}", hoje);

        gerarFluxoCaixaPorData(hoje);
    }

    // Calcular DRE baseada em contas a pagar/receber e folha
    private DRE calcularDRE(String periodo) {
        // Receitas
        Double receitaVendas = contaReceberRepository.findByPeriodo(periodo).stream()
                .mapToDouble(c -> c.getValorTotal().doubleValue()).sum();

        Double outrasReceitas = 0.0; // Placeholder - em produção, incluir outras receitas

        // Custo das Vendas (simulado - em produção, incluir custos diretos)
        Double custoVendas = 0.0;

        // Receita Total
        Double receitaTotal = receitaVendas + outrasReceitas;

        // Lucro Bruto
        Double lucroBruto = receitaTotal - custoVendas;

        // Despesas Operacionais
        // Folha de pagamento (custo operacional principal) - placeholder
        Double despesasFolha = 0.0;

        // Contas a pagar (despesas administrativas, comerciais, etc.)
        List<ContaPagar> contasPagar = contaPagarRepository.findByPeriodo(periodo);

        // Categorizar despesas pelo tipo de categoria
        Double despesasAdministrativas = contasPagar.stream()
                .filter(c -> c.getCategoria().name().contains("ADMINISTRATIVO") ||
                        c.getCategoria().name().contains("UTILIDADE") ||
                        c.getCategoria().name().contains("ALUGUEL"))
                .mapToDouble(c -> c.getValorTotal().doubleValue()).sum();

        Double despesasComerciais = contasPagar.stream()
                .filter(c -> c.getCategoria().name().contains("COMERCIAL") ||
                        c.getCategoria().name().contains("MARKETING") ||
                        c.getCategoria().name().contains("PUBLICIDADE"))
                .mapToDouble(c -> c.getValorTotal().doubleValue()).sum();

        Double despesasFinanceiras = contasPagar.stream()
                .filter(c -> c.getCategoria().name().contains("FINANCEIRO") ||
                        c.getCategoria().name().contains("JUROS") ||
                        c.getCategoria().name().contains("TAXA"))
                .mapToDouble(c -> c.getValorTotal().doubleValue()).sum();

        Double outrasDespesas = contasPagar.stream()
                .filter(c -> !c.getCategoria().name().contains("ADMINISTRATIVO") &&
                        !c.getCategoria().name().contains("COMERCIAL") &&
                        !c.getCategoria().name().contains("FINANCEIRO") &&
                        !c.getCategoria().name().contains("FOLHA"))
                .mapToDouble(c -> c.getValorTotal().doubleValue()).sum();

        // Despesas Total
        Double despesasTotal = despesasFolha + despesasAdministrativas +
                despesasComerciais + despesasFinanceiras + outrasDespesas;

        // Lucro Operacional (EBITDA)
        Double lucroOperacional = despesasTotal > 0 ? (receitaTotal - despesasTotal) : receitaTotal;

        // Resultado Financeiro ( Placeholder )
        Double resultadoFinanceiro = 0.0;

        // Lucro Antes do Imposto (EBIT)
        Double lucroAntesImposto = lucroOperacional + resultadoFinanceiro;

        // Impostos ( Placeholder - em produção, calcular tributos )
        Double impostos = 0.0;

        // Lucro Líquido
        Double lucroLiquido = lucroAntesImposto - impostos;

        return DRE.builder()
                .periodo(periodo)
                .receitaVendas(receitaVendas)
                .outrasReceitas(outrasReceitas)
                .receitaTotal(receitaTotal)
                .custoVendas(custoVendas)
                .lucroBruto(lucroBruto)
                .despesasAdministrativas(despesasAdministrativas)
                .despesasComerciais(despesasComerciais)
                .despesasFinanceiras(despesasFinanceiras)
                .outrasDespesas(outrasDespesas)
                .despesasTotal(despesasTotal)
                .lucroOperacional(lucroOperacional)
                .resultadoFinanceiro(resultadoFinanceiro)
                .lucroAntesImposto(lucroAntesImposto)
                .impostos(impostos)
                .lucroLiquido(lucroLiquido)
                .build();
    }

    // Gerar fluxo de caixa por dia
    private void gerarFluxoCaixaPorData(LocalDate data) {
        String periodo = YearMonth.from(data).toString();

        // Entradas (Contas a Receber pagas)
        List<ContaReceber> contasReceber = contaReceberRepository.findByVencimento(data);
        for (ContaReceber conta : contasReceber) {
            if (conta.getStatus() != null && conta.getStatus().name().equals("PAGO")) {
                FluxoCaixa fluxoEntrada = FluxoCaixa.builder()
                        .periodo(periodo)
                        .data(data)
                        .tipo("ENTRADA")
                        .categoria(conta.getCategoria().name())
                        .descricao("Recebimento: " + conta.getDescricao())
                        .valor(conta.getValorTotal().doubleValue())
                        .contaBancaria("Banco Principal")
                        .build();
                fluxoCaixaRepository.save(fluxoEntrada);
            }
        }

        // Saídas (Contas a Pagar pagas)
        List<ContaPagar> contasPagar = contaPagarRepository.findByVencimento(data);
        for (ContaPagar conta : contasPagar) {
            if (conta.getStatus() != null && conta.getStatus().name().equals("PAGO")) {
                FluxoCaixa fluxoSaida = FluxoCaixa.builder()
                        .periodo(periodo)
                        .data(data)
                        .tipo("SAIDA")
                        .categoria(conta.getCategoria().name())
                        .descricao("Pagamento: " + conta.getDescricao())
                        .valor(conta.getValorTotal().doubleValue())
                        .contaBancaria("Banco Principal")
                        .build();
                fluxoCaixaRepository.save(fluxoSaida);
            }
        }
    }

    // Listar DRE por período
    @Transactional(readOnly = true)
    public List<DRE> listarDRE(String periodoInicio, String periodoFim) {
        return dreRepository.findByPeriodoBetweenOrderByPeriodoDesc(periodoInicio, periodoFim);
    }

    // Listar Fluxo de Caixa por período
    @Transactional(readOnly = true)
    public List<FluxoCaixa> listarFluxoCaixa(String periodo) {
        return fluxoCaixaRepository.findByPeriodo(periodo);
    }

    // Saldo atual (último salário líquido + contas a receber - contas a pagar)
    @Transactional(readOnly = true)
    public Double getSaldoAtual() {
        // Placeholder - em produção, calcular saldo real
        return 0.0;
    }
}
