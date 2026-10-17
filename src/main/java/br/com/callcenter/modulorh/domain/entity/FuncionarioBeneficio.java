package br.com.callcenter.modulorh.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionario_beneficios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficio beneficio;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorFuncionario;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorEmpresa;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para criação rápida
    public FuncionarioBeneficio(Funcionario funcionario, Beneficio beneficio,
            LocalDate dataInicio, BigDecimal valorFuncionario,
            BigDecimal valorEmpresa) {
        this.funcionario = funcionario;
        this.beneficio = beneficio;
        this.dataInicio = dataInicio;
        this.valorFuncionario = valorFuncionario;
        this.valorEmpresa = valorEmpresa;
        this.ativo = true;
    }
}