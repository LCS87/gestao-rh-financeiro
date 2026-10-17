package br.com.callcenter.modulofinanceiro.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contas_pagar")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String fornecedor;

    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_pago", precision = 19, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConta status = StatusConta.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaConta categoria;

    @Column(length = 500)
    private String observacoes;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (valorPago == null) {
            valorPago = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
        if (valorPago == null) {
            valorPago = BigDecimal.ZERO;
        }
        // Atualiza status automaticamente
        if (valorPago.compareTo(valorTotal) >= 0) {
            status = StatusConta.PAGO;
        } else if (valorPago.compareTo(BigDecimal.ZERO) > 0) {
            status = StatusConta.PARCIAL;
        }
    }

    public BigDecimal getValorRestante() {
        return valorTotal.subtract(valorPago != null ? valorPago : BigDecimal.ZERO);
    }

    public boolean isVencida() {
        return status != StatusConta.PAGO && 
               LocalDate.now().isAfter(dataVencimento);
    }

    public enum StatusConta {
        PENDENTE, PARCIAL, PAGO, CANCELADO
    }

    public enum CategoriaConta {
        ALUGUEL, ENERGIA, AGUA, TELEFONE, INTERNET, MATERIAL_ESCRITORIO,
        EQUIPAMENTOS, SERVICOS_TERCEIROS, IMPOSTOS, FOLHA_PAGAMENTO,
        BENEFICIOS, OUTROS
    }
}
