package br.com.callcenter.modulorh.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Registro de pausa obrigatória para operadores de teleatendimento
 * (NR-17 / Lei 7.689 - jornadas de 50 min trabalho / 10 min descanso).
 */
public record PausaNR17Request(
        @NotNull(message = "funcionarioId e obrigatorio") java.util.UUID funcionarioId,
        @NotNull(message = "tipo e obrigatorio") TipoPausa tipo,
        String observacoes) {

    public enum TipoPausa {
        /** Pausa-descanso: 10 min a cada 50 min trabalhados (intermitencia). */
        DESCANSO_10MIN,
        /** Pausa-lanche: ate 20 min diarios. */
        LANCHE_20MIN
    }
}
