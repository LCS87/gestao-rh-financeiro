package br.com.callcenter.modulorh.api.dto;

import br.com.callcenter.modulorh.domain.entity.Funcionario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class FuncionarioDto {

    private UUID id;

    @NotBlank
    private String nome;

    @NotBlank
    private String cpf;

    private String email;

    private String telefone;

    @NotNull
    private LocalDate dataNascimento;

    private LocalDate dataAdmissao;

    private LocalDate dataDemissao;

    private Funcionario.StatusFuncionario status;

    private UUID departamentoId;
    private UUID cargoId;
    private UUID equipeId;
    private UUID escalaId;

    @PositiveOrZero
    private Double salarioBase;
}

