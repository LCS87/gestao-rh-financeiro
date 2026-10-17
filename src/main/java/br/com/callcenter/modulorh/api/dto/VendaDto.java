package br.com.callcenter.modulorh.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendaDto {
    private UUID id;
    private UUID vendedorId;
    private String vendedorNome;
    private BigDecimal valor;
    private LocalDateTime dataVenda;
}
