package br.com.callcenter.modulorelatorios.api;

import br.com.callcenter.modulo_comissoes.api.dto.ApuracaoComissaoResponse;
import br.com.callcenter.modulo_comissoes.application.ComissaoService;
import br.com.callcenter.modulorelatorios.application.RelatorioPdfService;
import br.com.callcenter.modulorelatorios.application.RelatoriosService;
import br.com.callcenter.modulorelatorios.domain.entity.DRE;
import br.com.callcenter.modulorelatorios.domain.entity.FluxoCaixa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relatórios Gerenciais", description = "DRE, Fluxo de Caixa e Relatórios Gerenciais")
public class RelatoriosController {

    private final RelatoriosService relatoriosService;
    private final ComissaoService comissaoService;
    private final RelatorioPdfService relatorioPdfService;

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

    // --- Exportação PDF dos relatórios gerenciais (Fase 4) ---

    @GetMapping(value = "/comissoes/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exporta apuração de comissões do mês em PDF")
    public ResponseEntity<byte[]> exportarComissoesPdf(@RequestParam String mesReferencia) {
        try {
            ApuracaoComissaoResponse apuracao = comissaoService.apurarComissoesMes(mesReferencia);
            List<String[]> linhas = new ArrayList<>();
            linhas.add(new String[]{"Funcionário", "Comissão", "SECAO"});
            linhas.add(new String[]{"Total de funcionários na apuração:",
                    nullSafe(apuracao.getFuncionarios().size())});
            for (ApuracaoComissaoResponse.ApuracaoPorFuncionario f : apuracao.getFuncionarios()) {
                linhas.add(new String[]{
                        f.getNomeFuncionario() + " (" + f.getQuantidadeVendas() + " vendas / R$ "
                                + fmt(f.getTotalVendas()) + ")",
                        "R$ " + fmt(f.getTotalComissoes())});
            }
            byte[] pdf = relatorioPdfService.gerarRelatorio(
                    "RELAÇÃO DE COMISSÕES POR OPERADOR",
                    "Competência: " + mesReferencia + " | Emitido em: " + LocalDate.now(),
                    linhas);
            return pdfResponse(pdf, "comissoes-" + mesReferencia + ".pdf");
        } catch (Exception e) {
            log.error("Falha ao gerar PDF de comissões", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/provisoes-folha/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exporta extrato de provisões da folha em PDF")
    public ResponseEntity<byte[]> exportarProvisoesPdf(@RequestParam String periodo) {
        try {
            Map<String, Object> prov = relatoriosService.extratoProvisoesFolha(periodo);
            List<String[]> linhas = new ArrayList<>();
            linhas.add(new String[]{"Item", "Valor", "SECAO"});
            linhas.add(new String[]{"Período:", nullSafe(prov.get("periodo"))});
            linhas.add(new String[]{"Quantidade de folhas:", nullSafe(prov.get("quantidadeFolhas"))});
            linhas.add(new String[]{"Provisão FGTS:", "R$ " + fmt(prov.get("provisaoFGTS"))});
            linhas.add(new String[]{"Provisão INSS:", "R$ " + fmt(prov.get("provisaoINSS"))});
            linhas.add(new String[]{"Provisão 13º Salário:", "R$ " + fmt(prov.get("provisaoDecimoTerceiro"))});
            linhas.add(new String[]{"Provisão Férias:", "R$ " + fmt(prov.get("provisaoFerias"))});
            linhas.add(new String[]{"Comissões a pagar:", "R$ " + fmt(prov.get("comissoesAPagar"))});
            linhas.add(new String[]{"Encargos estimados:", "R$ " + fmt(prov.get("encargosEstimados"))});
            linhas.add(new String[]{"TOTAL PROVISIONADO:", "R$ " + fmt(prov.get("totalProvisionado"))});
            byte[] pdf = relatorioPdfService.gerarRelatorio(
                    "EXTRATO DE PROVISÕES DA FOLHA",
                    "Período: " + periodo + " | Emitido em: " + LocalDate.now(),
                    linhas);
            return pdfResponse(pdf, "provisoes-" + periodo + ".pdf");
        } catch (Exception e) {
            log.error("Falha ao gerar PDF de provisões", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/turnover-absenteismo/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exporta turnover/absenteísmo por departamento em PDF")
    public ResponseEntity<byte[]> exportarTurnoverPdf(@RequestParam String periodo) {
        try {
            List<Map<String, Object>> linhasDados = relatoriosService.turnoverAbsenteismoPorDepartamento(periodo);
            List<String[]> linhas = new ArrayList<>();
            linhas.add(new String[]{"Departamento", "Efetivo / Desligados / Turnover% / Absenteismo%", "SECAO"});
            for (Map<String, Object> l : linhasDados) {
                linhas.add(new String[]{
                        nullSafe(l.get("departamentoId")),
                        nullSafe(l.get("totalFuncionarios")) + " / " +
                                nullSafe(l.get("desligadosNoPeriodo")) + " / " +
                                nullSafe(l.get("turnoverPercent")) + "% / " +
                                nullSafe(l.get("absenteismoPercent")) + "%"});
            }
            byte[] pdf = relatorioPdfService.gerarRelatorio(
                    "TURNOVER E ABSENTEÍSMO POR DEPARTAMENTO",
                    "Período: " + periodo + " | Emitido em: " + LocalDate.now(),
                    linhas);
            return pdfResponse(pdf, "turnover-absenteismo-" + periodo + ".pdf");
        } catch (Exception e) {
            log.error("Falha ao gerar PDF de turnover", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/dre/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Exporta DRE do período em PDF")
    public ResponseEntity<byte[]> exportarDREPdf(
            @RequestParam String periodoInicio,
            @RequestParam String periodoFim) {
        try {
            List<DRE> dres = relatoriosService.listarDRE(periodoInicio, periodoFim);
            List<String[]> linhas = new ArrayList<>();
            for (DRE d : dres) {
                linhas.add(new String[]{"PERÍODO " + nullSafe(d.getPeriodo()), "", "SECAO"});
                linhas.add(new String[]{"Receita de Vendas:", "R$ " + fmt(d.getReceitaVendas())});
                linhas.add(new String[]{"Outras Receitas:", "R$ " + fmt(d.getOutrasReceitas())});
                linhas.add(new String[]{"Receita Total:", "R$ " + fmt(d.getReceitaTotal())});
                linhas.add(new String[]{"Custo de Vendas:", "R$ " + fmt(d.getCustoVendas())});
                linhas.add(new String[]{"Lucro Bruto:", "R$ " + fmt(d.getLucroBruto())});
                linhas.add(new String[]{"Despesas Administrativas:", "R$ " + fmt(d.getDespesasAdministrativas())});
                linhas.add(new String[]{"Despesas Comerciais:", "R$ " + fmt(d.getDespesasComerciais())});
                linhas.add(new String[]{"Despesas Financeiras:", "R$ " + fmt(d.getDespesasFinanceiras())});
                linhas.add(new String[]{"Outras Despesas:", "R$ " + fmt(d.getOutrasDespesas())});
                linhas.add(new String[]{"Despesas Totais:", "R$ " + fmt(d.getDespesasTotal())});
                linhas.add(new String[]{"Lucro Operacional:", "R$ " + fmt(d.getLucroOperacional())});
                linhas.add(new String[]{"Resultado Financeiro:", "R$ " + fmt(d.getResultadoFinanceiro())});
                linhas.add(new String[]{"Lucro Antes de Impostos:", "R$ " + fmt(d.getLucroAntesImposto())});
                linhas.add(new String[]{"Impostos:", "R$ " + fmt(d.getImpostos())});
                linhas.add(new String[]{"LUCRO LÍQUIDO:", "R$ " + fmt(d.getLucroLiquido())});
            }
            if (linhas.isEmpty()) {
                linhas.add(new String[]{"Sem dados de DRE para o período informado.", ""});
            }
            byte[] pdf = relatorioPdfService.gerarRelatorio(
                    "DEMONSTRATIVO DE RESULTADO (DRE)",
                    "Período: " + periodoInicio + " a " + periodoFim + " | Emitido em: " + LocalDate.now(),
                    linhas);
            return pdfResponse(pdf, "dre-" + periodoInicio + "-" + periodoFim + ".pdf");
        } catch (Exception e) {
            log.error("Falha ao gerar PDF de DRE", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdf);
    }

    private static String fmt(Object v) {
        if (v == null) return "0,00";
        double d = (v instanceof Number n) ? n.doubleValue() : Double.parseDouble(String.valueOf(v));
        return String.format(java.util.Locale.US, "%.2f", d).replace('.', ',');
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
