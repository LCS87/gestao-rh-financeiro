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
@Table(name = "contas_receber")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaReceber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String cliente;

    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_recebido", precision = 19, scale = 2)
    private BigDecimal valorRecebido;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_recebimento")
    private LocalDate dataRecebimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConta status = StatusConta.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaReceita categoria;

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
        if (valorRecebido == null) {
            valorRecebido = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
        if (valorRecebido == null) {
            valorRecebido = BigDecimal.ZERO;
        }
        // Atualiza status automaticamente
        if (valorRecebido.compareTo(valorTotal) >= 0) {
            status = StatusConta.PAGO;
        } else if (valorRecebido.compareTo(BigDecimal.ZERO) > 0) {
            status = StatusConta.PARCIAL;
        }
    }

    public BigDecimal getValorRestante() {
        return valorTotal.subtract(valorRecebido != null ? valorRecebido : BigDecimal.ZERO);
    }

    public boolean isVencida() {
        return status != StatusConta.PAGO && 
               LocalDate.now().isAfter(dataVencimento);
    }

    public enum StatusConta {
        PENDENTE, PARCIAL, PAGO, CANCELADO
    }

    public enum CategoriaReceita {
        SERVICOS_CALL_CENTER, VENDAS_PRODUTOS, CONSULTORIA, 
        TAXAS_ADMINISTRACAO, MULTAS, JUROS, OUTROS
    }
}
