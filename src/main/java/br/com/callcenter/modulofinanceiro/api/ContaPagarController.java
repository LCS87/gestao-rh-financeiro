package br.com.callcenter.modulofinanceiro.api;

import br.com.callcenter.modulofinanceiro.application.ContaPagarService;
import br.com.callcenter.modulofinanceiro.domain.entity.ContaPagar;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/financeiro/contas-pagar")
public class ContaPagarController {

    private final ContaPagarService contaPagarService;

    public ContaPagarController(ContaPagarService contaPagarService) {
        this.contaPagarService = contaPagarService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> listarTodas() {
        return ResponseEntity.ok(contaPagarService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaPagar> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(contaPagarService.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> listarPorStatus(@PathVariable ContaPagar.StatusConta status) {
        return ResponseEntity.ok(contaPagarService.listarPorStatus(status));
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> listarPorCategoria(@PathVariable ContaPagar.CategoriaConta categoria) {
        return ResponseEntity.ok(contaPagarService.listarPorCategoria(categoria));
    }

    @GetMapping("/vencimento")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> listarPorVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(contaPagarService.listarPorVencimento(inicio, fim));
    }

    @GetMapping("/vencidas")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> listarVencidas() {
        return ResponseEntity.ok(contaPagarService.listarVencidas());
    }

    @GetMapping("/a-vencer")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> listarAVencer(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(contaPagarService.listarAVencerNoPeriodo(dias));
    }

    @GetMapping("/fornecedor/{fornecedor}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaPagar>> buscarPorFornecedor(@PathVariable String fornecedor) {
        return ResponseEntity.ok(contaPagarService.buscarPorFornecedor(fornecedor));
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','SUPERVISOR')")
    public ResponseEntity<Object> getResumo() {
        return ResponseEntity.ok(java.util.Map.of(
            "totalPendente", contaPagarService.calcularTotalPendente(),
            "vencimentosHoje", contaPagarService.contarVencimentosHoje()
        ));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaPagar> criar(@Valid @RequestBody ContaPagar conta) {
        ContaPagar criada = contaPagarService.criar(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaPagar> atualizar(@PathVariable UUID id, @Valid @RequestBody ContaPagar conta) {
        return ResponseEntity.ok(contaPagarService.atualizar(id, conta));
    }

    @PostMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaPagar> pagar(@PathVariable UUID id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaPagarService.pagar(id, valor));
    }

    @PostMapping("/{id}/pagar-total")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaPagar> pagarTotal(@PathVariable UUID id) {
        return ResponseEntity.ok(contaPagarService.pagarTotal(id));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        contaPagarService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        contaPagarService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
