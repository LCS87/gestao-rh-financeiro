package br.com.callcenter.modulorh.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação de enquete")
public class EnqueteRequestDTO {

    @NotNull(message = "ID do autor é obrigatório")
    @Schema(description = "ID do autor da enquete", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private String autorId;

    @NotBlank(message = "Pergunta da enquete é obrigatória")
    @Size(max = 500, message = "Pergunta não pode ter mais de 500 caracteres")
    @Schema(description = "Pergunta da enquete", example = "Qual seu horário preferido para reuniões?", required = true)
    private String pergunta;

    @Size(max = 1000, message = "Descrição não pode ter mais de 1000 caracteres")
    @Schema(description = "Descrição detalhada da enquete (opcional)", example = "Vamos escolher o melhor horário para nossas reuniões semanais")
    private String descricao;

    @Schema(description = "Se a enquete é anônima", example = "false", defaultValue = "false")
    private Boolean anonima = false;

    @Schema(description = "Se permite múltipla escolha", example = "false", defaultValue = "false")
    private Boolean permiteMultiplaEscolha = false;

    @Schema(description = "Data de expiração da enquete (opcional)", example = "2024-12-31T23:59:59")
    private LocalDateTime dataExpiracao;

    @NotEmpty(message = "É necessário pelo menos uma opção")
    @Schema(description = "Lista de opções para votação", example = "[\"Manhã (9h-12h)\", \"Tarde (14h-17h)\", \"Fim do dia (17h-18h)\"]", required = true)
    private List<@Size(max = 500, message = "Opção não pode ter mais de 500 caracteres") String> opcoes;
}