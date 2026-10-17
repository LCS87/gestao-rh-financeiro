package br.com.callcenter.modulorelatorios.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "fluxo_caixa_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FluxoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Período: YYYY-MM
    @Column(nullable = false, length = 7)
    private String periodo;

    // Data do lançamento
    @Column(nullable = false)
    private LocalDate data;

    // Tipo: ENTRADA ou SAIDA
    @Column(nullable = false, length = 10)
    private String tipo;

    // Categoria
    @Column(nullable = false, length = 50)
    private String categoria;

    // Descrição
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Valor
    @Column(nullable = false, precision = 19, scale = 2)
    private Double valor;

    // Saldo acumulado (opcional)
    @Column(name = "saldo_acumulado", precision = 19, scale = 2)
    private Double saldoAcumulado;

    // Conta bancária (opcional)
    @Column(name = "conta_bancaria", length = 100)
    private String contaBancaria;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}
