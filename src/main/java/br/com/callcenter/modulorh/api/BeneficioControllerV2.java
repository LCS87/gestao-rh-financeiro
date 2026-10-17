package br.com.callcenter.modulorh.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.callcenter.modulorh.api.dto.BeneficioDTO;
import br.com.callcenter.modulorh.api.dto.FuncionarioBeneficioRequestDTO;
import br.com.callcenter.modulorh.api.dto.FuncionarioBeneficioResponseDTO;
import br.com.callcenter.modulorh.application.service.BeneficioService;
import br.com.callcenter.modulorh.domain.entity.Beneficio;
import br.com.callcenter.modulorh.domain.entity.FuncionarioBeneficio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/beneficios")
@RequiredArgsConstructor
@Tag(name = "Gestão de Benefícios V2", description = "API aprimorada para gerenciamento de benefícios com DTOs")
public class BeneficioControllerV2 {

    private final BeneficioService beneficioService;

    // Beneficio CRUD com DTOs
    @PostMapping
    @Operation(summary = "Criar novo benefício usando DTO")
    public ResponseEntity<Beneficio> criarBeneficio(@Valid @RequestBody BeneficioDTO beneficioDTO) {
        Beneficio beneficio = new Beneficio();
        beneficio.setNome(beneficioDTO.getNome());
        beneficio.setDescricao(beneficioDTO.getDescricao());
        beneficio.setTipo(beneficioDTO.getTipo());
        beneficio.setTipoCobranca(beneficioDTO.getTipoCobranca());
        beneficio.setValor(beneficioDTO.getValor());
        beneficio.setPercentualDesconto(beneficioDTO.getPercentualDesconto());
        beneficio.setObrigatorio(beneficioDTO.getObrigatorio());

        return ResponseEntity.ok(beneficioService.criarBeneficio(beneficio));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício existente usando DTO")
    public ResponseEntity<Beneficio> atualizarBeneficio(
            @PathVariable UUID id, @Valid @RequestBody BeneficioDTO beneficioDTO) {

        Beneficio beneficioAtualizado = new Beneficio();
        beneficioAtualizado.setNome(beneficioDTO.getNome());
        beneficioAtualizado.setDescricao(beneficioDTO.getDescricao());
        beneficioAtualizado.setTipo(beneficioDTO.getTipo());
        beneficioAtualizado.setTipoCobranca(beneficioDTO.getTipoCobranca());
        beneficioAtualizado.setValor(beneficioDTO.getValor());
        beneficioAtualizado.setPercentualDesconto(beneficioDTO.getPercentualDesconto());
        beneficioAtualizado.setObrigatorio(beneficioDTO.getObrigatorio());
        beneficioAtualizado.setAtivo(true); // Mantém ativo

        return ResponseEntity.ok(beneficioService.atualizarBeneficio(id, beneficioAtualizado));
    }

    // Associação com funcionários usando DTOs
    @PostMapping("/associar")
    @Operation(summary = "Associar benefício a funcionário usando DTO")
    public ResponseEntity<FuncionarioBeneficioResponseDTO> associarBeneficioAFuncionario(
            @Valid @RequestBody FuncionarioBeneficioRequestDTO requestDTO) {

        FuncionarioBeneficio funcionarioBeneficio = beneficioService.adicionarBeneficioAFuncionario(
                requestDTO.getFuncionarioId(),
                requestDTO.getBeneficioId(),
                requestDTO.getDataInicio(),
                requestDTO.getDataFim());

        return ResponseEntity.ok(convertToResponseDTO(funcionarioBeneficio));
    }

    @GetMapping("/funcionarios/{funcionarioId}/detalhado")
    @Operation(summary = "Listar benefícios de um funcionário com detalhes")
    public ResponseEntity<List<FuncionarioBeneficioResponseDTO>> listarBeneficiosPorFuncionarioDetalhado(
            @PathVariable UUID funcionarioId) {

        List<FuncionarioBeneficio> beneficios = beneficioService.listarBeneficiosPorFuncionario(funcionarioId);
        List<FuncionarioBeneficioResponseDTO> responseDTOs = beneficios.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    // Métodos de conversão
    private FuncionarioBeneficioResponseDTO convertToResponseDTO(FuncionarioBeneficio funcionarioBeneficio) {
        FuncionarioBeneficioResponseDTO dto = new FuncionarioBeneficioResponseDTO();
        dto.setId(funcionarioBeneficio.getId());
        dto.setFuncionarioId(funcionarioBeneficio.getFuncionario().getId());
        dto.setFuncionarioNome(funcionarioBeneficio.getFuncionario().getNome());
        dto.setBeneficioId(funcionarioBeneficio.getBeneficio().getId());
        dto.setBeneficioNome(funcionarioBeneficio.getBeneficio().getNome());
        dto.setBeneficioTipo(funcionarioBeneficio.getBeneficio().getTipo());
        dto.setBeneficioTipoCobranca(funcionarioBeneficio.getBeneficio().getTipoCobranca());
        dto.setDataInicio(funcionarioBeneficio.getDataInicio());
        dto.setDataFim(funcionarioBeneficio.getDataFim());
        dto.setValorFuncionario(funcionarioBeneficio.getValorFuncionario());
        dto.setValorEmpresa(funcionarioBeneficio.getValorEmpresa());
        dto.setValorTotal(funcionarioBeneficio.getValorFuncionario() != null &&
                funcionarioBeneficio.getValorEmpresa() != null
                        ? funcionarioBeneficio.getValorFuncionario().add(funcionarioBeneficio.getValorEmpresa())
                        : BigDecimal.ZERO);
        dto.setAtivo(funcionarioBeneficio.getAtivo());
        dto.setDataCriacao(funcionarioBeneficio.getDataCriacao());
        dto.setDataAtualizacao(funcionarioBeneficio.getDataAtualizacao());

        return dto;
    }

    // Relatórios avançados
    @GetMapping("/relatorio/custo-mensal")
    @Operation(summary = "Gerar relatório detalhado de custos mensais")
    public ResponseEntity<String> gerarRelatorioCustoMensal(@RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        LocalDate dataReferencia = LocalDate.now();
        if (mes != null && ano != null) {
            dataReferencia = LocalDate.of(ano, mes, 1);
        }

        BigDecimal custoMensal = beneficioService.calcularCustoTotalMensalEmpresa();
        Long totalBeneficios = beneficioService.contarBeneficiosAtivos();

        String relatorio = String.format("""
                Relatório de Custos com Benefícios - %s
                ==========================================
                Data de Referência: %s
                Total de Benefícios Ativos: %d
                Custo Mensal Estimado: R$ %,.2f
                ==========================================
                """,
                dataReferencia.getMonth() + "/" + dataReferencia.getYear(),
                dataReferencia.toString(),
                totalBeneficios,
                custoMensal);

        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/dashboard/resumo")
    @Operation(summary = "Obter dados para dashboard de benefícios")
    public ResponseEntity<Object> obterDashboardResumo() {
        Long totalBeneficios = beneficioService.contarBeneficiosAtivos();
        BigDecimal custoMensal = beneficioService.calcularCustoTotalMensalEmpresa();
        Object[] resumo = beneficioService.obterResumoBeneficios();

        return ResponseEntity.ok(new DashboardResumo(
                totalBeneficios,
                custoMensal,
                resumo,
                LocalDate.now()));
    }

    // Classe interna para o dashboard
    private static class DashboardResumo {
        public final Long totalBeneficios;
        public final BigDecimal custoMensalEmpresa;
        public final Object resumoDetalhado;
        public final LocalDate dataAtualizacao;

        DashboardResumo(Long totalBeneficios, BigDecimal custoMensalEmpresa,
                Object resumoDetalhado, LocalDate dataAtualizacao) {
            this.totalBeneficios = totalBeneficios;
            this.custoMensalEmpresa = custoMensalEmpresa;
            this.resumoDetalhado = resumoDetalhado;
            this.dataAtualizacao = dataAtualizacao;
        }
    }
}