package br.com.callcenter.modulorh.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Vinculação em lote de funcionários a uma escala (call center:
 * remanejamento de equipe inteira para 6x1, 12x36 etc.).
 */
public record VinculoEscalaLoteRequest(
        @NotNull(message = "escalaId e obrigatorio") UUID escalaId,
        @NotEmpty(message = "funcionarioIds nao pode ser vazio") List<UUID> funcionarioIds) {
}
