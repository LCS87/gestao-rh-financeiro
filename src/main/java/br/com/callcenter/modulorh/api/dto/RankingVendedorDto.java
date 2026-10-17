package br.com.callcenter.modulorh.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingVendedorDto {
    private UUID vendedorId;
    private String vendedorNome;
    private BigDecimal vendasDiarias;
    private BigDecimal vendasSemanais;
    private BigDecimal vendasMensais;
    private BigDecimal metaDiaria;
}
