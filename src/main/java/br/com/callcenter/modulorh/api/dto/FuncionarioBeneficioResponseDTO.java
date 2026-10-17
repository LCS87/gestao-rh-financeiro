package br.com.callcenter.modulorh.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.callcenter.modulorh.domain.enums.TipoBeneficio;
import br.com.callcenter.modulorh.domain.enums.TipoCobrancaBeneficio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para resposta de associação de benefício com funcionário")
public class FuncionarioBeneficioResponseDTO {

    @Schema(description = "ID da associação", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID id;

    @Schema(description = "ID do funcionário", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID funcionarioId;

    @Schema(description = "Nome do funcionário", example = "João Silva")
    private String funcionarioNome;

    @Schema(description = "ID do benefício", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID beneficioId;

    @Schema(description = "Nome do benefício", example = "Vale Transporte")
    private String beneficioNome;

    @Schema(description = "Tipo do benefício", example = "VALE_TRANSPORTE")
    private TipoBeneficio beneficioTipo;

    @Schema(description = "Tipo de cobrança do benefício", example = "VALOR_FIXO")
    private TipoCobrancaBeneficio beneficioTipoCobranca;

    @Schema(description = "Data de início do benefício", example = "2024-01-01")
    private LocalDate dataInicio;

    @Schema(description = "Data de fim do benefício", example = "2024-12-31")
    private LocalDate dataFim;

    @Schema(description = "Valor pago pelo funcionário", example = "125.00")
    private BigDecimal valorFuncionario;

    @Schema(description = "Valor pago pela empresa", example = "125.00")
    private BigDecimal valorEmpresa;

    @Schema(description = "Valor total do benefício", example = "250.00")
    private BigDecimal valorTotal;

    @Schema(description = "Indica se a associação está ativa", example = "true")
    private Boolean ativo;

    @Schema(description = "Data de criação da associação", example = "2024-01-01T10:00:00")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data de atualização da associação", example = "2024-01-01T10:00:00")
    private LocalDateTime dataAtualizacao;
}