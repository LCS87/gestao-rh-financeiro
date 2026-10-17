package br.com.callcenter.modulo_comissoes.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.callcenter.modulo_comissoes.application.ComissaoService;
import br.com.callcenter.modulo_comissoes.domain.entity.Meta;
import br.com.callcenter.modulo_comissoes.domain.entity.RegraComissao;
import br.com.callcenter.modulo_comissoes.domain.entity.VendaComissao;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comissoes")
@RequiredArgsConstructor
@Tag(name = "Comissões e Metas", description = "Gestão de comissões e metas de vendas")
public class ComissaoController {

    private final ComissaoService comissaoService;

    // --- Regras de Comissão ---

    @GetMapping("/regras")
    @PreAuthorize("hasAnyRole('ADMIN','RH','FINANCEIRO')")
    public ResponseEntity<List<RegraComissao>> listarRegrasAtivas() {
        List<RegraComissao> regras = comissaoService.listarRegrasAtivas();
        return ResponseEntity.ok(regras);
    }

    @GetMapping("/regras/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','FINANCEIRO')")
    public ResponseEntity<RegraComissao> buscarRegraPorId(@PathVariable UUID id) {
        RegraComissao regra = comissaoService.buscarRegraPorId(id);
        return ResponseEntity.ok(regra);
    }

    @PostMapping("/regras")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<RegraComissao> criarRegra(@RequestBody RegraComissao regra) {
        RegraComissao criada = comissaoService.criarRegra(regra);
        return ResponseEntity.ok(criada);
    }

    @PutMapping("/regras/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<RegraComissao> atualizarRegra(@PathVariable UUID id, @RequestBody RegraComissao regra) {
        RegraComissao atualizada = comissaoService.atualizarRegra(id, regra);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/regras/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> desativarRegra(@PathVariable UUID id) {
        comissaoService.desativarRegra(id);
        return ResponseEntity.noContent().build();
    }

    // --- Cálculo de Comissões ---

    @PostMapping("/vendas/{vendaId}/comissao/{regraId}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<VendaComissao> calcularComissaoVenda(
            @PathVariable UUID vendaId,
            @PathVariable UUID regraId) {
        VendaComissao vendaComissao = comissaoService.calcularComissaoVenda(vendaId, regraId);
        return ResponseEntity.ok(vendaComissao);
    }

    @PostMapping("/comissao/{vendaComissaoId}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCEIRO')")
    public ResponseEntity<Void> pagarComissao(@PathVariable UUID vendaComissaoId) {
        comissaoService.pagarComissao(vendaComissaoId);
        return ResponseEntity.noContent().build();
    }

    // --- Metas ---

    @GetMapping("/funcionarios/{funcionarioId}/metas")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Meta>> listarMetasFuncionario(@PathVariable UUID funcionarioId) {
        List<Meta> metas = comissaoService.listarMetasFuncionario(funcionarioId);
        return ResponseEntity.ok(metas);
    }

    @PostMapping("/metas")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Meta> criarMeta(@RequestBody Meta meta) {
        Meta criada = comissaoService.criarMeta(meta);
        return ResponseEntity.ok(criada);
    }

    @PutMapping("/metas/{metaId}/valor")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Meta> atualizarValorMeta(@PathVariable UUID metaId, @RequestBody BigDecimal valor) {
        Meta atualizada = comissaoService.atualizarValorMeta(metaId, valor);
        return ResponseEntity.ok(atualizada);
    }

    @PutMapping("/metas/{metaId}/concluir")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Meta> concluirMeta(@PathVariable UUID metaId) {
        Meta concluida = comissaoService.concluirMeta(metaId);
        return ResponseEntity.ok(concluida);
    }

    // --- Ranking ---

    @GetMapping("/ranking")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<ComissaoService.RankingVendedor>> getRankingVendedores(
            @RequestParam(defaultValue = "30") int dias) {
        List<ComissaoService.RankingVendedor> ranking = comissaoService.getRankingVendedores(dias);
        return ResponseEntity.ok(ranking);
    }
}
