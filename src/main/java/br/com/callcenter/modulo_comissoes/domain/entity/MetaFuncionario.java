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
@Table(name = "metas_funcionarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_id", nullable = false)
    private Meta meta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "valor_meta_individual", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorMetaIndividual;

    @Column(name = "valor_alcancado_individual", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAlcancadoIndividual = BigDecimal.ZERO;

    @Column(name = "percentual_conclusao", precision = 5, scale = 2)
    private BigDecimal percentualConclusao = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private String status = "ATIVA";

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void atualizarValorAlcancado(BigDecimal novoValor) {
        this.valorAlcancadoIndividual = novoValor;
        BigDecimal percentual = calcularPercentualConclusao();
        this.percentualConclusao = percentual;

        if (novoValor.compareTo(this.valorMetaIndividual) >= 0) {
            this.status = "CONCLUIDA";
            this.dataConclusao = LocalDateTime.now();
        }
    }

    public BigDecimal calcularPercentualConclusao() {
        if (valorMetaIndividual.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorAlcancadoIndividual.multiply(BigDecimal.valueOf(100))
                .divide(valorMetaIndividual, 2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean isMetaBatida() {
        return valorAlcancadoIndividual.compareTo(valorMetaIndividual) >= 0;
    }
}
