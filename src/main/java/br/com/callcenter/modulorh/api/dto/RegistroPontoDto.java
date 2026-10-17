package br.com.callcenter.modulorh.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class RegistroPontoDto {

    private UUID id;

    @NotNull
    private UUID funcionarioId;

    @NotNull
    private LocalDate data;

    @NotNull
    private LocalTime horaEntrada;

    private LocalTime horaIntervaloInicio;
    private LocalTime horaIntervaloFim;
    private LocalTime horaSaida;

    private String observacoes;
}
