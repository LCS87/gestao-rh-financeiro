package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.enums.TipoCicloAvaliacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação ou atualização de ciclo de avaliação")
public class CicloAvaliacaoDTO {

    @NotBlank(message = "O nome do ciclo é obrigatório")
    @Size(max = 100, message = "O nome do ciclo deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do ciclo de avaliação", example = "Avaliação Trimestral Q1 2026")
    private String nome;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição detalhada do ciclo", example = "Avaliação de desempenho do primeiro trimestre de 2026")
    private String descricao;

    @NotNull(message = "O tipo do ciclo é obrigatório")
    @Schema(description = "Tipo do ciclo de avaliação", example = "TRIMESTRAL")
    private TipoCicloAvaliacao tipo;

    @NotNull(message = "O ano de referência é obrigatório")
    @Min(value = 2020, message = "O ano de referência deve ser a partir de 2020")
    @Max(value = 2100, message = "O ano de referência deve ser até 2100")
    @Schema(description = "Ano de referência do ciclo", example = "2026")
    private Integer anoReferencia;

    @Min(value = 1, message = "O período de referência deve ser no mínimo 1")
    @Max(value = 4, message = "O período de referência deve ser no máximo 4")
    @Schema(description = "Período de referência (1-4 para trimestral, 1-2 para semestral)", example = "1")
    private Integer periodoReferencia;

    @NotNull(message = "A data de início é obrigatória")
    @FutureOrPresent(message = "A data de início deve ser igual ou posterior à data atual")
    @Schema(description = "Data de início do ciclo", example = "2026-01-01")
    private LocalDate dataInicio;

    @NotNull(message = "A data de fim é obrigatória")
    @Future(message = "A data de fim deve ser posterior à data atual")
    @Schema(description = "Data de fim do ciclo", example = "2026-03-31")
    private LocalDate dataFim;

    @Schema(description = "Data de início da autoavaliação", example = "2026-01-01")
    private LocalDate dataInicioAutoavaliacao;

    @Schema(description = "Data de fim da autoavaliação", example = "2026-01-14")
    private LocalDate dataFimAutoavaliacao;

    @Schema(description = "Data de início da avaliação do gestor", example = "2026-01-15")
    private LocalDate dataInicioAvaliacaoGestor;

    @Schema(description = "Data de fim da avaliação do gestor", example = "2026-01-31")
    private LocalDate dataFimAvaliacaoGestor;

    @Schema(description = "Data de início do feedback", example = "2026-02-01")
    private LocalDate dataInicioFeedback;

    @Schema(description = "Data de fim do feedback", example = "2026-02-15")
    private LocalDate dataFimFeedback;

    @Schema(description = "ID do formulário de autoavaliação", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID formularioAutoavaliacaoId;

    @Schema(description = "ID do formulário de avaliação do gestor", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID formularioAvaliacaoGestorId;

    @Schema(description = "Indica se o ciclo está ativo", example = "true")
    private Boolean ativo = true;
}