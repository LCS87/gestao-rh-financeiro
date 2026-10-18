package br.com.callcenter.modulo_comissoes.api.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Request para simulação de comissão (Fase 4 — Regras de Comissões).
 */
@Data
public class SimulacaoComissaoRequest {

    /** Valor da venda a simular. */
    private BigDecimal valorVenda;

    /** Percentual da meta batida (0-200), usado por aceleradores e bônus de meta. */
    private BigDecimal percentualMeta = BigDecimal.ZERO;
}
