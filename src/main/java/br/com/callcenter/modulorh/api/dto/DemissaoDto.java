package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Demissao;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemissaoDto {
    private UUID id;
    
    @NotNull(message = "ID do funcionário é obrigatório")
    private UUID funcionarioId;
    
    @NotNull(message = "Data de demissão é obrigatória")
    private LocalDate dataDemissao;
    
    @NotNull(message = "Tipo de demissão é obrigatório")
    private Demissao.TipoDemissao tipoDemissao;
    
    private Demissao.StatusDemissao status;
    
    private Boolean avisoPrevioTrabalhado;
    private Boolean avisoPrevioIndenizado;
    private Boolean exameDemissionalRealizado;
    private LocalDate dataExameDemissional;
    private String motivo;
    private String observacoes;
    
    // Valores calculados
    private BigDecimal saldoSalario;
    private BigDecimal feriasVencidas;
    private BigDecimal feriasProporcionais;
    private BigDecimal decimoTerceiro;
    private BigDecimal decimoTerceiroProporcional;
    private BigDecimal avisoPrevioIndenizadoValor;
    private BigDecimal fgts;
    private BigDecimal multaFgts;
    private BigDecimal valorTotalRescisao;
    private LocalDate dataCalculo;
    
    private UUID responsavelDemissaoId;
}
