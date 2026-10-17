package br.com.callcenter.modulorh.api.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class EquipeDto {
    private UUID id;
    private String nome;
    private UUID supervisorId;
    private UUID departamentoId;
}

