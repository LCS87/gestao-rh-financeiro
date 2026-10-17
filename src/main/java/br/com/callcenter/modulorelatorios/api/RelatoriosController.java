package br.com.callcenter.modulorelatorios.api;

import br.com.callcenter.modulorelatorios.application.RelatoriosService;
import br.com.callcenter.modulorelatorios.domain.entity.DRE;
import br.com.callcenter.modulorelatorios.domain.entity.FluxoCaixa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios Gerenciais", description = "DRE, Fluxo de Caixa e Relatórios Gerenciais")
public class RelatoriosController {

    private final RelatoriosService relatoriosService;

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
