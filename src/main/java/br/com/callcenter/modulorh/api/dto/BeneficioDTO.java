package br.com.callcenter.modulorh.api.dto;

import java.math.BigDecimal;

import br.com.callcenter.modulorh.domain.enums.TipoBeneficio;
import br.com.callcenter.modulorh.domain.enums.TipoCobrancaBeneficio;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação e atualização de benefícios")
public class BeneficioDTO {

    @NotBlank(message = "Nome do benefício é obrigatório")
    @Size(max = 100, message = "Nome não pode ter mais de 100 caracteres")
    @Schema(description = "Nome do benefício", example = "Vale Transporte", required = true)
    private String nome;

    @Size(max = 500, message = "Descrição não pode ter mais de 500 caracteres")
    @Schema(description = "Descrição detalhada do benefício", example = "Auxílio para transporte público ou combustível")
    private String descricao;

    @NotNull(message = "Tipo do benefício é obrigatório")
    @Schema(description = "Tipo do benefício", example = "VALE_TRANSPORTE", required = true)
    private TipoBeneficio tipo;

    @NotNull(message = "Tipo de cobrança é obrigatório")
    @Schema(description = "Tipo de cobrança do benefício", example = "VALOR_FIXO", required = true)
    private TipoCobrancaBeneficio tipoCobranca;

    @Schema(description = "Valor fixo do benefício (para tipos VALOR_FIXO, EMPRESA_CUSTEIA_TOTAL, etc.)", example = "250.00")
    private BigDecimal valor;

    @Schema(description = "Percentual de desconto sobre salário (para tipo PERCENTUAL_SALARIO)", example = "5.00")
    private BigDecimal percentualDesconto;

    @Schema(description = "Indica se o benefício é obrigatório para todos os funcionários", example = "true")
    private Boolean obrigatorio = false;
}