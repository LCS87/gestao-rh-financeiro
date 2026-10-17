package br.com.callcenter.modulo_comissoes.domain.entity;

import br.com.callcenter.modulorh.domain.entity.Funcionario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vendas_comissao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaComissao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private br.com.callcenter.modulorh.domain.entity.Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regra_comissao_id", nullable = false)
    private RegraComissao regraComissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorVenda;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorComissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false, length = 50)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(length = 500)
    private String observacoes;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void pagarComissao() {
        this.statusPagamento = StatusPagamento.PAGO;
        this.dataPagamento = LocalDateTime.now();
    }

    public void cancelarComissao() {
        this.statusPagamento = StatusPagamento.CANCELADO;
    }

    public boolean isPaga() {
        return statusPagamento == StatusPagamento.PAGO;
    }
}
