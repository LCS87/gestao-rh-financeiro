package br.com.callcenter.modulo_comissoes.api.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Apuração mensal de comissões por funcionário (Fase 4).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApuracaoComissaoResponse {

    private String mesReferencia;
    private BigDecimal totalGeralComissoes;
    private long quantidadeVendas;
    private List<ApuracaoPorFuncionario> funcionarios;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApuracaoPorFuncionario {
        private UUID funcionarioId;
        private String nomeFuncionario;
        private UUID departamentoId;
        private BigDecimal totalVendas;
        private BigDecimal totalComissoes;
        private long quantidadeVendas;
    }
}
