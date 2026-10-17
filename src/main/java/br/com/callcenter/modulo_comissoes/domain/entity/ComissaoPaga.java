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
@Table(name = "comissoes_pagas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComissaoPaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_comissao_id", nullable = false)
    private VendaComissao vendaComissao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorComissao;

    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private String status = "PAGO";

    @Column(length = 500)
    private String observacoes;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public ComissaoPaga(VendaComissao vendaComissao, String mesReferencia, LocalDateTime dataPagamento) {
        this.vendaComissao = vendaComissao;
        this.funcionario = vendaComissao.getFuncionario();
        this.valorComissao = vendaComissao.getValorComissao();
        this.mesReferencia = mesReferencia;
        this.dataPagamento = dataPagamento;
        this.dataCriacao = LocalDateTime.now();
    }
}
