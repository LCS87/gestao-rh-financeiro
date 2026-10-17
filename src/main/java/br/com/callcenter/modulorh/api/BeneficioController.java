package br.com.callcenter.modulorh.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.callcenter.modulorh.application.service.BeneficioService;
import br.com.callcenter.modulorh.domain.entity.Beneficio;
import br.com.callcenter.modulorh.domain.entity.FuncionarioBeneficio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/beneficios")
@RequiredArgsConstructor
@Tag(name = "Gestão de Benefícios", description = "API para gerenciamento de benefícios e associação com funcionários")
public class BeneficioController {

    private final BeneficioService beneficioService;

    // Endpoints para Beneficios
    @GetMapping
    @Operation(summary = "Listar todos os benefícios ativos")
    public ResponseEntity<List<Beneficio>> listarBeneficiosAtivos() {
        return ResponseEntity.ok(beneficioService.listarBeneficiosAtivos());
    }

    @GetMapping("/opcionais")
    @Operation(summary = "Listar benefícios opcionais ativos")
    public ResponseEntity<List<Beneficio>> listarBeneficiosOpcionaisAtivos() {
        return ResponseEntity.ok(beneficioService.listarBeneficiosOpcionaisAtivos());
    }

    @GetMapping("/obrigatorios")
    @Operation(summary = "Listar benefícios obrigatórios ativos")
    public ResponseEntity<List<Beneficio>> listarBeneficiosObrigatoriosAtivos() {
        return ResponseEntity.ok(beneficioService.listarBeneficiosObrigatoriosAtivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID")
    public ResponseEntity<Beneficio> buscarBeneficioPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(beneficioService.buscarBeneficioPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício")
    public ResponseEntity<Beneficio> criarBeneficio(@RequestBody Beneficio beneficio) {
        return ResponseEntity.ok(beneficioService.criarBeneficio(beneficio));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício existente")
    public ResponseEntity<Beneficio> atualizarBeneficio(
            @PathVariable UUID id, @RequestBody Beneficio beneficio) {
        return ResponseEntity.ok(beneficioService.atualizarBeneficio(id, beneficio));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar benefício (soft delete)")
    public ResponseEntity<Void> desativarBeneficio(@PathVariable UUID id) {
        beneficioService.desativarBeneficio(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints para associação com funcionários
    @PostMapping("/funcionarios/{funcionarioId}/beneficios/{beneficioId}")
    @Operation(summary = "Adicionar benefício a um funcionário")
    public ResponseEntity<FuncionarioBeneficio> adicionarBeneficioAFuncionario(
            @PathVariable UUID funcionarioId,
            @PathVariable UUID beneficioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return ResponseEntity.ok(beneficioService.adicionarBeneficioAFuncionario(
                funcionarioId, beneficioId, dataInicio, dataFim));
    }

    @GetMapping("/funcionarios/{funcionarioId}")
    @Operation(summary = "Listar benefícios de um funcionário")
    public ResponseEntity<List<FuncionarioBeneficio>> listarBeneficiosPorFuncionario(
            @PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(beneficioService.listarBeneficiosPorFuncionario(funcionarioId));
    }

    @DeleteMapping("/funcionarios/{funcionarioId}/beneficios/{beneficioId}")
    @Operation(summary = "Remover benefício de um funcionário")
    public ResponseEntity<Void> removerBeneficioDeFuncionario(
            @PathVariable UUID funcionarioId,
            @PathVariable UUID beneficioId) {

        beneficioService.removerBeneficioDeFuncionario(funcionarioId, beneficioId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/funcionarios/{funcionarioId}/beneficios/{beneficioId}/data-fim")
    @Operation(summary = "Atualizar data de fim de um benefício para funcionário")
    public ResponseEntity<Void> atualizarDataFimBeneficio(
            @PathVariable UUID funcionarioId,
            @PathVariable UUID beneficioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate novaDataFim) {

        beneficioService.atualizarDataFimBeneficio(funcionarioId, beneficioId, novaDataFim);
        return ResponseEntity.noContent().build();
    }

    // Endpoints de consulta e relatórios
    @GetMapping("/contagem/ativos")
    @Operation(summary = "Contar benefícios ativos")
    public ResponseEntity<Long> contarBeneficiosAtivos() {
        return ResponseEntity.ok(beneficioService.contarBeneficiosAtivos());
    }

    @GetMapping("/{id}/contagem-funcionarios")
    @Operation(summary = "Contar funcionários com um benefício específico")
    public ResponseEntity<Long> contarFuncionariosComBeneficio(@PathVariable UUID id) {
        return ResponseEntity.ok(beneficioService.contarFuncionariosComBeneficio(id));
    }

    @GetMapping("/custo-mensal-empresa")
    @Operation(summary = "Calcular custo mensal total da empresa com benefícios")
    public ResponseEntity<String> calcularCustoTotalMensalEmpresa() {
        return ResponseEntity.ok("Custo mensal: R$ " +
                beneficioService.calcularCustoTotalMensalEmpresa().toString());
    }

    @GetMapping("/resumo")
    @Operation(summary = "Obter resumo consolidado de benefícios")
    public ResponseEntity<Object[]> obterResumoBeneficios() {
        return ResponseEntity.ok(beneficioService.obterResumoBeneficios());
    }
}