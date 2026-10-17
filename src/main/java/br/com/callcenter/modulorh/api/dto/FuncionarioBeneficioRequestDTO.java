package br.com.callcenter.modulorh.api.dto;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para associação de benefício com funcionário")
public class FuncionarioBeneficioRequestDTO {

    @NotNull(message = "ID do funcionário é obrigatório")
    @Schema(description = "ID do funcionário", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID funcionarioId;

    @NotNull(message = "ID do benefício é obrigatório")
    @Schema(description = "ID do benefício", example = "123e4567-e89b-12d3-a456-426614174001", required = true)
    private UUID beneficioId;

    @NotNull(message = "Data de início é obrigatória")
    @Schema(description = "Data de início do benefício para o funcionário", example = "2024-01-01", required = true)
    private LocalDate dataInicio;

    @Schema(description = "Data de fim do benefício (opcional)", example = "2024-12-31")
    private LocalDate dataFim;
}