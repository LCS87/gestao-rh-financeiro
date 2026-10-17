package br.com.callcenter.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametroSistemaDto {
    private String chave;
    private String valor;
    private String descricao;
    private String tipo;
}
