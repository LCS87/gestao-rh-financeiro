package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.HistoricoVendasDto;
import br.com.callcenter.modulorh.api.dto.RankingVendedorDto;
import br.com.callcenter.modulorh.api.dto.ResumoVendasDto;
import br.com.callcenter.modulorh.api.dto.VendaDto;
import br.com.callcenter.modulorh.application.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/vendas")
@RequiredArgsConstructor
@Tag(name = "Vendas", description = "API de Gestão de Vendas (Fase 4)")
public class VendaController {

    private final VendaService vendaService;

    public record NovaVendaRequest(UUID sellerId, BigDecimal amount) {}

    @PostMapping
    @Operation(summary = "Registrar nova venda")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VISUALIZADOR')")
    public ResponseEntity<VendaDto> registrarVenda(@RequestBody NovaVendaRequest request) {
        VendaDto venda = vendaService.registrarVenda(request.sellerId(), request.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }

    @GetMapping
    @Operation(summary = "Lista histórico de todas as vendas brutos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<VendaDto>> listarVendas() {
        return ResponseEntity.ok(vendaService.listarVendas());
    }

    @GetMapping("/resumo-mensal")
    @Operation(summary = "Resumo global do Dashboard (Total Mensal, Semanal, Diário)")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VISUALIZADOR', 'FINANCEIRO', 'RH')")
    public ResponseEntity<ResumoVendasDto> getResumoMensal() {
        return ResponseEntity.ok(vendaService.getResumoVendas());
    }

    @GetMapping("/ranking-diario")
    @Operation(summary = "Ranking entre os vendedores e Meta base Diária cruzada")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'VISUALIZADOR', 'FINANCEIRO', 'RH')")
    public ResponseEntity<List<RankingVendedorDto>> getRankingDiario() {
        return ResponseEntity.ok(vendaService.getRankingDiario());
    }

    @GetMapping("/historico-global")
    @Operation(summary = "Gráfico Recharts: Histórico diário global de vendas")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'TI')")
    public ResponseEntity<List<HistoricoVendasDto>> getHistoricoGlobal(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(vendaService.getHistoricoGlobal(dias));
    }

    @GetMapping("/historico-pessoal/{id}")
    @Operation(summary = "Gráfico Recharts: Histórico diário pessoal de vendas")
    @PreAuthorize("hasAnyRole('VISUALIZADOR', 'ADMIN', 'SUPERVISOR', 'TI')")
    public ResponseEntity<List<HistoricoVendasDto>> getHistoricoPessoal(@PathVariable UUID id, @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(vendaService.getHistoricoPessoal(id, dias));
    }
}
