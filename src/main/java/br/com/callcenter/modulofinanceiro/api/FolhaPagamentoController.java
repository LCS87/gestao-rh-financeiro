package br.com.callcenter.modulofinanceiro.api;

import br.com.callcenter.modulofinanceiro.application.FolhaPagamentoService;
import br.com.callcenter.modulofinanceiro.domain.entity.FolhaPagamento;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/financeiro/folha-pagamento")
public class FolhaPagamentoController {

    private final FolhaPagamentoService folhaPagamentoService;

    public FolhaPagamentoController(FolhaPagamentoService folhaPagamentoService) {
        this.folhaPagamentoService = folhaPagamentoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<List<FolhaPagamento>> listarTodas() {
        return ResponseEntity.ok(folhaPagamentoService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<FolhaPagamento> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(folhaPagamentoService.buscarPorId(id));
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<List<FolhaPagamento>> listarPorFuncionario(@PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(folhaPagamentoService.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/mes/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<List<FolhaPagamento>> listarPorMes(@PathVariable String anoMes) {
        YearMonth yearMonth = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        return ResponseEntity.ok(folhaPagamentoService.listarPorMes(yearMonth));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<List<FolhaPagamento>> listarPorStatus(@PathVariable FolhaPagamento.StatusFolha status) {
        return ResponseEntity.ok(folhaPagamentoService.listarPorStatus(status));
    }

    @GetMapping("/funcionario/{funcionarioId}/mes/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<FolhaPagamento> buscarPorFuncionarioEMes(
            @PathVariable UUID funcionarioId,
            @PathVariable String anoMes) {
        YearMonth yearMonth = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        return ResponseEntity.ok(folhaPagamentoService.buscarPorFuncionarioEMes(funcionarioId, yearMonth));
    }

    @GetMapping("/resumo/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getResumoMes(@PathVariable String anoMes) {
        YearMonth yearMonth = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        return ResponseEntity.ok(Map.of(
            "totalFolha", folhaPagamentoService.calcularTotalFolha(yearMonth),
            "processando", folhaPagamentoService.contarProcessandoNoMes(yearMonth)
        ));
    }

    @PostMapping("/funcionario/{funcionarioId}/mes/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<FolhaPagamento> criarFolhaFuncionario(
            @PathVariable UUID funcionarioId,
            @PathVariable String anoMes) {
        YearMonth yearMonth = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        FolhaPagamento criada = folhaPagamentoService.criarFolhaFuncionario(funcionarioId, yearMonth);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PostMapping("/gerar-lote/{anoMes}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<Void> gerarFolhaEmLote(@PathVariable String anoMes) {
        YearMonth yearMonth = YearMonth.parse(anoMes, DateTimeFormatter.ofPattern("yyyy-MM"));
        folhaPagamentoService.gerarFolhaEmLote(yearMonth);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/aplicar-comissoes/{mesReferencia}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<String> aplicarComissoesNaFolha(@PathVariable String mesReferencia) {
        int aplicadas = folhaPagamentoService.aplicarComissoesNaFolha(mesReferencia);
        return ResponseEntity.ok(aplicadas + " folha(s) atualizada(s) com as comissões apuradas de " + mesReferencia);
    }

    @PostMapping("/{id}/processar")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<FolhaPagamento> processarFolha(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") Integer numeroDependentes) {
        return ResponseEntity.ok(folhaPagamentoService.processarFolha(id, numeroDependentes));
    }

    @PostMapping("/{id}/proventos")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<FolhaPagamento> adicionarProvento(
            @PathVariable UUID id,
            @RequestParam String tipoProvento,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(folhaPagamentoService.adicionarProvento(id, tipoProvento, valor));
    }

    @PostMapping("/{id}/descontos")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','RH')")
    public ResponseEntity<FolhaPagamento> adicionarDesconto(
            @PathVariable UUID id,
            @RequestParam String tipoDesconto,
            @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(folhaPagamentoService.adicionarDesconto(id, tipoDesconto, valor));
    }

    @PostMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<FolhaPagamento> pagarFolha(
            @PathVariable UUID id,
            @RequestParam String banco,
            @RequestParam String agencia,
            @RequestParam String conta) {
        return ResponseEntity.ok(folhaPagamentoService.pagarFolha(id, banco, agencia, conta));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        folhaPagamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
