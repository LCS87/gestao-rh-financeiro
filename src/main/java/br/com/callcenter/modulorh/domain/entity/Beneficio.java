package br.com.callcenter.modulorh.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.callcenter.modulorh.domain.enums.TipoBeneficio;
import br.com.callcenter.modulorh.domain.enums.TipoCobrancaBeneficio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "beneficios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoBeneficio tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoCobrancaBeneficio tipoCobranca;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean obrigatorio = false;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor para criação rápida
    public Beneficio(String nome, TipoBeneficio tipo, TipoCobrancaBeneficio tipoCobranca,
            BigDecimal valor, Boolean obrigatorio) {
        this.nome = nome;
        this.tipo = tipo;
        this.tipoCobranca = tipoCobranca;
        this.valor = valor;
        this.obrigatorio = obrigatorio;
        this.ativo = true;
    }
}