package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Ferias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class FeriasDto {

    private UUID id;

    @NotNull
    private UUID funcionarioId;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate dataFim;

    @NotNull
    @Positive
    private Integer dias;

    private Ferias.TipoFerias tipo = Ferias.TipoFerias.INTEGRAL;
    private Ferias.StatusFerias status = Ferias.StatusFerias.AGENDADA;

    private String observacoes;
}
