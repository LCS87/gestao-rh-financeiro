package br.com.callcenter.modulorh.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação ou atualização de resposta de avaliação")
public class RespostaAvaliacaoDTO {

    @NotNull(message = "O ID da avaliação é obrigatório")
    @Schema(description = "ID da avaliação de desempenho", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID avaliacaoId;

    @NotNull(message = "O ID da questão é obrigatório")
    @Schema(description = "ID da questão de avaliação", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID questaoId;

    @Schema(description = "Resposta (texto ou opção selecionada)", example = "Excelente desempenho")
    private String resposta;

    @Schema(description = "Nota (para questões numéricas)", example = "4.5")
    private BigDecimal nota;

    @Schema(description = "Justificativa para a resposta", example = "O funcionário demonstrou grande habilidade técnica")
    private String justificativa;
}