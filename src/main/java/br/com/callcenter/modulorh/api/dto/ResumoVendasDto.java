package br.com.callcenter.modulorh.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumoVendasDto {
    private BigDecimal totalMensal;
    private BigDecimal totalSemanal;
    private BigDecimal totalDiario;
}
