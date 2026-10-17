package br.com.callcenter.modulo_comissoes.domain.entity;

import br.com.callcenter.modulorh.domain.entity.Funcionario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "metas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoMeta tipoMeta;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorMeta;

    @Column(name = "valor_alcancado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAlcancado = BigDecimal.ZERO;

    @Column(name = "periodo_referencia", nullable = false, length = 7)
    private String periodoReferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusMeta status = StatusMeta.ATIVA;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL)
    private List<MetaFuncionario> metasFuncionarios = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void atualizarValorAlcancado(BigDecimal novoValor) {
        this.valorAlcancado = novoValor;
        if (novoValor.compareTo(this.valorMeta) >= 0) {
            this.status = StatusMeta.CONCLUIDA;
            this.dataConclusao = LocalDateTime.now();
        }
    }

    public BigDecimal calcularPercentualConclusao() {
        if (valorMeta.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorAlcancado.multiply(BigDecimal.valueOf(100))
                .divide(valorMeta, 2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean isMetaBatida() {
        return valorAlcancado.compareTo(valorMeta) >= 0;
    }
}
