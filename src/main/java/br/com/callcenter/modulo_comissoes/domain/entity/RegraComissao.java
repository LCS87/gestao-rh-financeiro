package br.com.callcenter.modulo_comissoes.domain.entity;

import br.com.callcenter.modulorh.domain.entity.Departamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "regras_comissao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegraComissao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoComissao tipoComissao;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentagem = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorFixo;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorMinimo;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorMaximo;

    /**
     * Acelerador: percentual EXTRA sobre a comissão quando a meta é batida
     * (ex.: 20 => +20% de comissão em campanhas de vendas). Nulo/zera sem acelerador.
     */
    @Column(name = "acelerador_percentual", precision = 5, scale = 2)
    private BigDecimal aceleradorPercentual;

    /**
     * Meta mínima (%) que precisa ser atingida para o acelerador/campanha valer.
     */
    @Column(name = "meta_minima_percentual", precision = 5, scale = 2)
    private BigDecimal metaMinimaPercentual;

    @Column(name = "aplicar_por_departamento", nullable = false)
    private Boolean aplicarPorDepartamento = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public RegraComissao(String nome, TipoComissao tipoComissao) {
        this.nome = nome;
        this.tipoComissao = tipoComissao;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    public BigDecimal calcularComissao(BigDecimal valorVenda) {
        return aplicarAcelerador(calcularComissaoBase(valorVenda), BigDecimal.ZERO);
    }

    /**
     * Cálculo base por tipo de regra.
     */
    private BigDecimal calcularComissaoBase(BigDecimal valorVenda) {
        BigDecimal comissao = BigDecimal.ZERO;

        switch (tipoComissao) {
            case PORCENTAGEM_VENDA:
                if (porcentagem != null) {
                    comissao = valorVenda.multiply(porcentagem).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                    break;
                }
            case VALOR_FIXO:
                comissao = valorFixo != null ? valorFixo : BigDecimal.ZERO;
                break;
            case FAIXA_VENDA:
                if (valorMinimo != null && valorMaximo != null) {
                    if (valorVenda.compareTo(valorMinimo) >= 0 && valorVenda.compareTo(valorMaximo) <= 0) {
                        comissao = valorFixo != null ? valorFixo : BigDecimal.ZERO;
                    }
                }
                break;
            case BONUS_META:
                // Será implementado na lógica de cálculo
                comissao = BigDecimal.ZERO;
                break;
        }

        return comissao;
    }

    /**
     * Simulação considerando o percentual da meta batida (campanhas/acceleradores):
     * - BONUS_META: paga valorFixo como bônus quando a meta mínima é atingida;
     * - aceleradorPercentual: multiplica a comissão extra quando a meta mínima é atingida.
     */
    public BigDecimal simularComissao(BigDecimal valorVenda, BigDecimal percentualMeta) {
        BigDecimal pctMeta = percentualMeta != null ? percentualMeta : BigDecimal.ZERO;
        BigDecimal comissao = calcularComissaoBase(valorVenda);

        if (tipoComissao == TipoComissao.BONUS_META && atingeMetaMinima(pctMeta) && valorFixo != null) {
            comissao = comissao.add(valorFixo);
        }

        return aplicarAcelerador(comissao, pctMeta);
    }

    private boolean atingeMetaMinima(BigDecimal percentualMeta) {
        BigDecimal minimo = metaMinimaPercentual != null ? metaMinimaPercentual : BigDecimal.valueOf(100);
        return percentualMeta.compareTo(minimo) >= 0;
    }

    private BigDecimal aplicarAcelerador(BigDecimal comissao, BigDecimal percentualMeta) {
        if (aceleradorPercentual == null || aceleradorPercentual.compareTo(BigDecimal.ZERO) <= 0) {
            return comissao;
        }
        if (!atingeMetaMinima(percentualMeta)) {
            return comissao;
        }
        BigDecimal extra = comissao.multiply(aceleradorPercentual)
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        return comissao.add(extra);
    }
}
