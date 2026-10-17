package br.com.callcenter.modulorh.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

@Data
public class CargoDto {
    private UUID id;
    @NotBlank
    private String nome;
    private String descricao;
    @PositiveOrZero
    private Double salarioBase;
}

