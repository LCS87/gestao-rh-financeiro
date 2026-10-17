package br.com.callcenter.modulorh.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculoRescisaoRequest {
    @NotNull(message = "ID do funcionário é obrigatório")
    private UUID funcionarioId;
    
    @NotNull(message = "Data de demissão é obrigatória")
    private LocalDate dataDemissao;
    
    @NotNull(message = "Tipo de demissão é obrigatório")
    private String tipoDemissao;
    
    private Boolean avisoPrevioTrabalhado;
    private Boolean avisoPrevioIndenizado;
    
    // Parâmetros opcionais para cálculo
    private Integer diasTrabalhados;
    private Integer mesesTrabalhados;
    private Boolean temFeriasVencidas;
}
