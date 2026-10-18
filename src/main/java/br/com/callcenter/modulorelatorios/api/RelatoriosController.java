package br.com.callcenter.modulorelatorios.api;

import br.com.callcenter.modulo_comissoes.api.dto.ApuracaoComissaoResponse;
import br.com.callcenter.modulo_comissoes.application.ComissaoService;
import br.com.callcenter.modulorelatorios.application.RelatoriosService;
import br.com.callcenter.modulorelatorios.domain.entity.DRE;
import br.com.callcenter.modulorelatorios.domain.entity.FluxoCaixa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios Gerenciais", description = "DRE, Fluxo de Caixa e Relatórios Gerenciais")
public class RelatoriosController {

    private final RelatoriosService relatoriosService;
    private final ComissaoService comissaoService;

    @GetMapping("/dre")
    @Operation(summary = "Lista DRE por período")
    public ResponseEntity<List<DRE>> listarDRE(
            @RequestParam String periodoInicio,
            @RequestParam String periodoFim) {
        return ResponseEntity.ok(relatoriosService.listarDRE(periodoInicio, periodoFim));
    }

    @GetMapping("/dre/ultimo")
    @Operation(summary = "Última DRE disponível")
    public ResponseEntity<DRE> ultimaDRE() {
        List<DRE> dres = relatoriosService.listarDRE("2024-01", "2026-12");
        if (!dres.isEmpty()) {
            return ResponseEntity.ok(dres.get(0));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/fluxo-caixa")
    @Operation(summary = "Lista fluxo de caixa por período")
    public ResponseEntity<List<FluxoCaixa>> listarFluxoCaixa(@RequestParam String periodo) {
        return ResponseEntity.ok(relatoriosService.listarFluxoCaixa(periodo));
    }

    @GetMapping("/fluxo-caixa/hoje")
    @Operation(summary = "Fluxo de caixa do dia")
    public ResponseEntity<List<FluxoCaixa>> fluxoCaixaDoDia() {
        String hoje = java.time.LocalDate.now().toString();
        return ResponseEntity.ok(relatoriosService.listarFluxoCaixa(hoje));
    }

    @GetMapping("/fluxo-caixa/resumo")
    @Operation(summary = "Resumo do fluxo de caixa por tipo (ENTRADA/SAIDA)")
    public ResponseEntity<FluxoCaixaResumo> resumoFluxoCaixa(@RequestParam String periodo) {
        List<FluxoCaixa> entradas = relatoriosService.listarFluxoCaixa(periodo).stream()
                .filter(f -> "ENTRADA".equals(f.getTipo()))
                .toList();
        List<FluxoCaixa> saidas = relatoriosService.listarFluxoCaixa(periodo).stream()
                .filter(f -> "SAIDA".equals(f.getTipo()))
                .toList();

        Double totalEntradas = entradas.stream().mapToDouble(FluxoCaixa::getValor).sum();
        Double totalSaidas = saidas.stream().mapToDouble(FluxoCaixa::getValor).sum();

        FluxoCaixaResumo resumo = FluxoCaixaResumo.builder()
                .periodo(periodo)
                .totalEntradas(totalEntradas)
                .totalSaidas(totalSaidas)
                .saldo(totalEntradas - totalSaidas)
                .quantidadeEntradas(entradas.size())
                .quantidadeSaidas(saidas.size())
                .build();

        return ResponseEntity.ok(resumo);
    }

    // --- Relatórios gerenciais com exportação CSV (Fase 4) ---

    @GetMapping("/comissoes/export")
    @Operation(summary = "Exporta apuração de comissões do mês em CSV")
    public ResponseEntity<String> exportarComissoesCsv(@RequestParam String mesReferencia) {
        ApuracaoComissaoResponse apuracao = comissaoService.apurarComissoesMes(mesReferencia);
        StringBuilder csv = new StringBuilder();
        csv.append("Funcionario;DepartamentoId;Qtde Vendas;Total Vendas;Total Comissoes\n");
        for (ApuracaoComissaoResponse.ApuracaoPorFuncionario f : apuracao.getFuncionarios()) {
            csv.append(String.format("%s;%s;%d;%s;%s%n",
                    escaparCsv(f.getNomeFuncionario()),
                    f.getDepartamentoId(),
                    f.getQuantidadeVendas(),
                    f.getTotalVendas(),
                    f.getTotalComissoes()));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"comissoes-" + mesReferencia + ".csv\"")
                .body(csv.toString());
    }

    @GetMapping("/provisoes-folha")
    @Operation(summary = "Extrato de provisões da folha (FGTS, INSS, 13º, férias e encargos)")
    public ResponseEntity<Map<String, Object>> extratoProvisoesFolha(@RequestParam String periodo) {
        return ResponseEntity.ok(relatoriosService.extratoProvisoesFolha(periodo));
    }

    @GetMapping("/provisoes-folha/export")
    @Operation(summary = "Exporta extrato de provisões da folha em CSV")
    public ResponseEntity<String> exportarProvisoesCsv(@RequestParam String periodo) {
        Map<String, Object> prov = relatoriosService.extratoProvisoesFolha(periodo);
        StringBuilder csv = new StringBuilder();
        csv.append("Item;Valor\n");
        csv.append("Periodo;").append(prov.get("periodo")).append('\n');
        csv.append("Quantidade de folhas;").append(prov.get("quantidadeFolhas")).append('\n');
        csv.append("Provisão FGTS;").append(prov.get("provisaoFGTS")).append('\n');
        csv.append("Provisão INSS;").append(prov.get("provisaoINSS")).append('\n');
        csv.append("Provisão 13º;").append(prov.get("provisaoDecimoTerceiro")).append('\n');
        csv.append("Provisão Férias;").append(prov.get("provisaoFerias")).append('\n');
        csv.append("Comissões a pagar;").append(prov.get("comissoesAPagar")).append('\n');
        csv.append("Encargos estimados;").append(prov.get("encargosEstimados")).append('\n');
        csv.append("Total provisionado;").append(prov.get("totalProvisionado")).append('\n');
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"provisoes-" + periodo + ".csv\"")
                .body(csv.toString());
    }

    @GetMapping("/turnover-absenteismo")
    @Operation(summary = "Turnover e absenteísmo por departamento no mês (YYYY-MM)")
    public ResponseEntity<List<Map<String, Object>>> turnoverAbsenteismo(@RequestParam String periodo) {
        return ResponseEntity.ok(relatoriosService.turnoverAbsenteismoPorDepartamento(periodo));
    }

    @GetMapping("/turnover-absenteismo/export")
    @Operation(summary = "Exporta turnover/absenteísmo por departamento em CSV")
    public ResponseEntity<String> exportarTurnoverCsv(@RequestParam String periodo) {
        List<Map<String, Object>> linhas = relatoriosService.turnoverAbsenteismoPorDepartamento(periodo);
        StringBuilder csv = new StringBuilder();
        csv.append("DepartamentoId;Total Funcionarios;Desligados;Turnover(%);Dias Afastamento;Absenteismo(%)\n");
        for (Map<String, Object> l : linhas) {
            csv.append(String.format("%s;%s;%s;%s;%s;%s%n",
                    l.get("departamentoId"), l.get("totalFuncionarios"),
                    l.get("desligadosNoPeriodo"), l.get("turnoverPercent"),
                    l.get("diasAfastamento"), l.get("absenteismoPercent")));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"turnover-absenteismo-" + periodo + ".csv\"")
                .body(csv.toString());
    }

    @GetMapping("/dre/export")
    @Operation(summary = "Exporta DRE do período em CSV")
    public ResponseEntity<String> exportarDRECsv(
            @RequestParam String periodoInicio,
            @RequestParam String periodoFim) {
        List<DRE> dres = relatoriosService.listarDRE(periodoInicio, periodoFim);
        StringBuilder csv = new StringBuilder();
        csv.append("Periodo;Receita Vendas;Outras Receitas;Receita Total;Custo Vendas;Lucro Bruto;")
           .append("Despesas Administrativas;Despesas Comerciais;Despesas Financeiras;Outras Despesas;")
           .append("Despesas Total;Lucro Operacional;Resultado Financeiro;Lucro Antes Imposto;Impostos;Lucro Liquido\n");
        for (DRE d : dres) {
            csv.append(String.join(";",
                    nullSafe(d.getPeriodo()), nullSafe(d.getReceitaVendas()), nullSafe(d.getOutrasReceitas()),
                    nullSafe(d.getReceitaTotal()), nullSafe(d.getCustoVendas()), nullSafe(d.getLucroBruto()),
                    nullSafe(d.getDespesasAdministrativas()), nullSafe(d.getDespesasComerciais()),
                    nullSafe(d.getDespesasFinanceiras()), nullSafe(d.getOutrasDespesas()),
                    nullSafe(d.getDespesasTotal()), nullSafe(d.getLucroOperacional()),
                    nullSafe(d.getResultadoFinanceiro()), nullSafe(d.getLucroAntesImposto()),
                    nullSafe(d.getImpostos()), nullSafe(d.getLucroLiquido()))).append('\n');
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"dre-" + periodoInicio + "-" + periodoFim + ".csv\"")
                .body(csv.toString());
    }

    @GetMapping("/saldo-atual")
    @Operation(summary = "Saldo atual consolidado (folha paga + entradas - saídas)")
    public ResponseEntity<Double> saldoAtual() {
        return ResponseEntity.ok(relatoriosService.getSaldoAtual());
    }

    private static String nullSafe(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private static String escaparCsv(String valor) {
        if (valor == null) return "";
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    // DTO para resumo de fluxo de caixa
    public static class FluxoCaixaResumo {
        private String periodo;
        private Double totalEntradas;
        private Double totalSaidas;
        private Double saldo;
        private Integer quantidadeEntradas;
        private Integer quantidadeSaidas;

        // Getters e Setters
        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }

        public Double getTotalEntradas() { return totalEntradas; }
        public void setTotalEntradas(Double totalEntradas) { this.totalEntradas = totalEntradas; }

        public Double getTotalSaidas() { return totalSaidas; }
        public void setTotalSaidas(Double totalSaidas) { this.totalSaidas = totalSaidas; }

        public Double getSaldo() { return saldo; }
        public void setSaldo(Double saldo) { this.saldo = saldo; }

        public Integer getQuantidadeEntradas() { return quantidadeEntradas; }
        public void setQuantidadeEntradas(Integer quantidadeEntradas) { this.quantidadeEntradas = quantidadeEntradas; }

        public Integer getQuantidadeSaidas() { return quantidadeSaidas; }
        public void setQuantidadeSaidas(Integer quantidadeSaidas) { this.quantidadeSaidas = quantidadeSaidas; }

        // Builder
        public static FluxoCaixaResumoBuilder builder() {
            return new FluxoCaixaResumoBuilder();
        }

        public static class FluxoCaixaResumoBuilder {
            private FluxoCaixaResumo resumo;

            public FluxoCaixaResumoBuilder() {
                this.resumo = new FluxoCaixaResumo();
            }

            public FluxoCaixaResumoBuilder periodo(String periodo) {
                resumo.setPeriodo(periodo);
                return this;
            }

            public FluxoCaixaResumoBuilder totalEntradas(Double totalEntradas) {
                resumo.setTotalEntradas(totalEntradas);
                return this;
            }

            public FluxoCaixaResumoBuilder totalSaidas(Double totalSaidas) {
                resumo.setTotalSaidas(totalSaidas);
                return this;
            }

            public FluxoCaixaResumoBuilder saldo(Double saldo) {
                resumo.setSaldo(saldo);
                return this;
            }

            public FluxoCaixaResumoBuilder quantidadeEntradas(Integer quantidadeEntradas) {
                resumo.setQuantidadeEntradas(quantidadeEntradas);
                return this;
            }

            public FluxoCaixaResumoBuilder quantidadeSaidas(Integer quantidadeSaidas) {
                resumo.setQuantidadeSaidas(quantidadeSaidas);
                return this;
            }

            public FluxoCaixaResumo build() {
                return resumo;
            }
        }
    }
}
