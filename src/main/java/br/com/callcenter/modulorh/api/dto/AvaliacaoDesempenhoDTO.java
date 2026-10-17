package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.enums.TipoAvaliacaoDesempenho;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação de avaliação de desempenho")
public class AvaliacaoDesempenhoDTO {

    @NotNull(message = "O ID do ciclo é obrigatório")
    @Schema(description = "ID do ciclo de avaliação", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID cicloId;

    @NotNull(message = "O ID do funcionário é obrigatório")
    @Schema(description = "ID do funcionário a ser avaliado", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID funcionarioId;

    @Schema(description = "ID do avaliador (obrigatório para avaliação do gestor)", 
            example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID avaliadorId;

    @NotNull(message = "O tipo de avaliação é obrigatório")
    @Schema(description = "Tipo de avaliação", example = "AUTOAVALIACAO")
    private TipoAvaliacaoDesempenho tipo;
}