package br.com.callcenter.modulorelatorios.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dre_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DRE {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Período: YYYY-MM
    @Column(nullable = false, length = 7)
    private String periodo;

    // Receitas
    @Column(name = "receita_vendas", precision = 19, scale = 2)
    private Double receitaVendas;

    @Column(name = "outras_receitas", precision = 19, scale = 2)
    private Double outrasReceitas;

    // Receita Total
    @Column(name = "receita_total", precision = 19, scale = 2)
    private Double receitaTotal;

    // Custo das Vendas (COGS)
    @Column(name = "custo_vendas", precision = 19, scale = 2)
    private Double custoVendas;

    // Lucro Bruto
    @Column(name = "lucro_bruto", precision = 19, scale = 2)
    private Double lucroBruto;

    // Despesas Operacionais
    @Column(name = "despesas_administrativas", precision = 19, scale = 2)
    private Double despesasAdministrativas;

    @Column(name = "despesas_comerciais", precision = 19, scale = 2)
    private Double despesasComerciais;

    @Column(name = "despesas_financeiras", precision = 19, scale = 2)
    private Double despesasFinanceiras;

    @Column(name = "outras_despesas", precision = 19, scale = 2)
    private Double outrasDespesas;

    // Despesas Total
    @Column(name = "despesas_total", precision = 19, scale = 2)
    private Double despesasTotal;

    // Lucro Operacional (EBITDA)
    @Column(name = "lucro_operacional", precision = 19, scale = 2)
    private Double lucroOperacional;

    // Resultado Financeiro
    @Column(name = "resultado_financeiro", precision = 19, scale = 2)
    private Double resultadoFinanceiro;

    // Lucro Antes do Imposto (EBIT)
    @Column(name = "lucro_antes_imposto", precision = 19, scale = 2)
    private Double lucroAntesImposto;

    // Impostos
    @Column(name = "impostos", precision = 19, scale = 2)
    private Double impostos;

    // Lucro Líquido
    @Column(name = "lucro_liquido", precision = 19, scale = 2)
    private Double lucroLiquido;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
