package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Escala;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

@Data
public class EscalaDto {

    private UUID id;

    @NotBlank
    private String nome;

    @NotNull
    private Escala.TipoEscala tipo;

    private String descricao;

    @PositiveOrZero
    private Double horasSemanais = 44.0;
}
