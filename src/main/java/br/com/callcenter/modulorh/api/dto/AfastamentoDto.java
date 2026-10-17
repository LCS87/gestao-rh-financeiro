package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Afastamento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AfastamentoDto {

    private UUID id;

    @NotNull
    private UUID funcionarioId;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    @NotNull
    private Afastamento.TipoAfastamento tipo;

    private String motivo;
    private String observacoes;
}
