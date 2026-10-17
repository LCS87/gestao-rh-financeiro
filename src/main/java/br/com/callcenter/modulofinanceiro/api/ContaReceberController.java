package br.com.callcenter.modulofinanceiro.api;

import br.com.callcenter.modulofinanceiro.application.ContaReceberService;
import br.com.callcenter.modulofinanceiro.domain.entity.ContaReceber;
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
@RequestMapping("/financeiro/contas-receber")
public class ContaReceberController {

    private final ContaReceberService contaReceberService;

    public ContaReceberController(ContaReceberService contaReceberService) {
        this.contaReceberService = contaReceberService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> listarTodas() {
        return ResponseEntity.ok(contaReceberService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaReceber> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(contaReceberService.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> listarPorStatus(@PathVariable ContaReceber.StatusConta status) {
        return ResponseEntity.ok(contaReceberService.listarPorStatus(status));
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> listarPorCategoria(@PathVariable ContaReceber.CategoriaReceita categoria) {
        return ResponseEntity.ok(contaReceberService.listarPorCategoria(categoria));
    }

    @GetMapping("/vencimento")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> listarPorVencimento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(contaReceberService.listarPorVencimento(inicio, fim));
    }

    @GetMapping("/vencidas")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> listarVencidas() {
        return ResponseEntity.ok(contaReceberService.listarVencidas());
    }

    @GetMapping("/a-vencer")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> listarAVencer(@RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(contaReceberService.listarAVencerNoPeriodo(dias));
    }

    @GetMapping("/cliente/{cliente}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<List<ContaReceber>> buscarPorCliente(@PathVariable String cliente) {
        return ResponseEntity.ok(contaReceberService.buscarPorCliente(cliente));
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO','SUPERVISOR')")
    public ResponseEntity<Object> getResumo() {
        return ResponseEntity.ok(java.util.Map.of(
            "totalPendente", contaReceberService.calcularTotalPendente(),
            "vencimentosHoje", contaReceberService.contarVencimentosHoje()
        ));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaReceber> criar(@Valid @RequestBody ContaReceber conta) {
        ContaReceber criada = contaReceberService.criar(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaReceber> atualizar(@PathVariable UUID id, @Valid @RequestBody ContaReceber conta) {
        return ResponseEntity.ok(contaReceberService.atualizar(id, conta));
    }

    @PostMapping("/{id}/receber")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaReceber> receber(@PathVariable UUID id, @RequestParam BigDecimal valor) {
        return ResponseEntity.ok(contaReceberService.receber(id, valor));
    }

    @PostMapping("/{id}/receber-total")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<ContaReceber> receberTotal(@PathVariable UUID id) {
        return ResponseEntity.ok(contaReceberService.receberTotal(id));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        contaReceberService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        contaReceberService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
