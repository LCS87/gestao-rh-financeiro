package br.com.callcenter.modulorh.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class DepartamentoDto {
    private UUID id;
    @NotBlank
    private String nome;
    private String descricao;
}

