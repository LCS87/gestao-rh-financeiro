package br.com.callcenter.modulofinanceiro.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "folhas_pagamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolhaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "funcionario_id", nullable = false)
    private UUID funcionarioId;

    @Column(name = "ano_mes", nullable = false, length = 7)
    private String anoMes; // Formato YYYY-MM

    @Column(name = "salario_base", nullable = false, precision = 19, scale = 2)
    private BigDecimal salarioBase;

    // Proventos
    @Column(name = "horas_extras", precision = 19, scale = 2)
    private BigDecimal horasExtras;

    @Column(name = "adicional_noturno", precision = 19, scale = 2)
    private BigDecimal adicionalNoturno;

    @Column(name = "comissoes", precision = 19, scale = 2)
    private BigDecimal comissoes;

    @Column(name = "bonus", precision = 19, scale = 2)
    private BigDecimal bonus;

    @Column(name = "ferias", precision = 19, scale = 2)
    private BigDecimal ferias;

    @Column(name = "decimo_terceiro", precision = 19, scale = 2)
    private BigDecimal decimoTerceiro;

    @Column(name = "outros_proventos", precision = 19, scale = 2)
    private BigDecimal outrosProventos;

    // Descontos
    @Column(name = "inss", precision = 19, scale = 2)
    private BigDecimal inss;

    @Column(name = "irrf", precision = 19, scale = 2)
    private BigDecimal irrf;

    @Column(name = "fgts", precision = 19, scale = 2)
    private BigDecimal fgts;

    @Column(name = "vale_transporte", precision = 19, scale = 2)
    private BigDecimal valeTransporte;

    @Column(name = "vale_refeicao", precision = 19, scale = 2)
    private BigDecimal valeRefeicao;

    @Column(name = "plano_saude", precision = 19, scale = 2)
    private BigDecimal planoSaude;

    @Column(name = "outros_descontos", precision = 19, scale = 2)
    private BigDecimal outrosDescontos;

    @Column(name = "total_proventos", precision = 19, scale = 2)
    private BigDecimal totalProventos;

    @Column(name = "total_descontos", precision = 19, scale = 2)
    private BigDecimal totalDescontos;

    @Column(name = "salario_liquido", precision = 19, scale = 2)
    private BigDecimal salarioLiquido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFolha status = StatusFolha.PROCESSANDO;

    @Column(name = "data_processamento")
    private LocalDate dataProcessamento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "banco_pagamento")
    private String bancoPagamento;

    @Column(name = "agencia_pagamento")
    private String agenciaPagamento;

    @Column(name = "conta_pagamento")
    private String contaPagamento;

    @Column(length = 1000)
    private String observacoes;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        calcularTotais();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
        calcularTotais();
    }

    private void calcularTotais() {
        BigDecimal proventos = BigDecimal.ZERO;
        if (salarioBase != null) proventos = proventos.add(salarioBase);
        if (horasExtras != null) proventos = proventos.add(horasExtras);
        if (adicionalNoturno != null) proventos = proventos.add(adicionalNoturno);
        if (comissoes != null) proventos = proventos.add(comissoes);
        if (bonus != null) proventos = proventos.add(bonus);
        if (ferias != null) proventos = proventos.add(ferias);
        if (decimoTerceiro != null) proventos = proventos.add(decimoTerceiro);
        if (outrosProventos != null) proventos = proventos.add(outrosProventos);

        BigDecimal descontos = BigDecimal.ZERO;
        if (inss != null) descontos = descontos.add(inss);
        if (irrf != null) descontos = descontos.add(irrf);
        if (fgts != null) descontos = descontos.add(fgts);
        if (valeTransporte != null) descontos = descontos.add(valeTransporte);
        if (valeRefeicao != null) descontos = descontos.add(valeRefeicao);
        if (planoSaude != null) descontos = descontos.add(planoSaude);
        if (outrosDescontos != null) descontos = descontos.add(outrosDescontos);

        this.totalProventos = proventos;
        this.totalDescontos = descontos;
        this.salarioLiquido = proventos.subtract(descontos);
    }

    /**
     * Recalcula explicitamente os totais (usado pela integração de comissões da Folha).
     */
    public void recalcularTotais() {
        calcularTotais();
    }

    public enum StatusFolha {
        PROCESSANDO, PROCESSADA, PAGA, CANCELADA
    }
}
