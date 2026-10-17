package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.StatusAvaliacaoDesempenho;
import br.com.callcenter.modulorh.domain.enums.TipoAvaliacaoDesempenho;
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
@Table(name = "avaliacoes_desempenho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoDesempenho {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private CicloAvaliacao ciclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliador_id")
    private Funcionario avaliador; // Pode ser o gestor ou autoavaliação (null)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAvaliacaoDesempenho tipo = TipoAvaliacaoDesempenho.AUTOAVALIACAO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusAvaliacaoDesempenho status = StatusAvaliacaoDesempenho.PENDENTE;

    @Column(name = "nota_final", precision = 5, scale = 2)
    private BigDecimal notaFinal;

    @Column(name = "percentual_conclusao", precision = 5, scale = 2)
    private BigDecimal percentualConclusao = BigDecimal.ZERO;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @Column(length = 2000)
    private String comentariosAvaliador;

    @Column(length = 2000)
    private String comentariosFuncionario;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespostaAvaliacao> respostas = new ArrayList<>();

    @OneToOne(mappedBy = "avaliacao", cascade = CascadeType.ALL)
    private PlanoDesenvolvimentoIndividual planoDesenvolvimento;

    @OneToOne(mappedBy = "avaliacao", cascade = CascadeType.ALL)
    private FeedbackAvaliacao feedback;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtores auxiliares
    public AvaliacaoDesempenho(CicloAvaliacao ciclo, Funcionario funcionario, 
                              TipoAvaliacaoDesempenho tipo) {
        this.ciclo = ciclo;
        this.funcionario = funcionario;
        this.tipo = tipo;
        this.status = StatusAvaliacaoDesempenho.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
        this.percentualConclusao = BigDecimal.ZERO;
    }

    public AvaliacaoDesempenho(CicloAvaliacao ciclo, Funcionario funcionario, 
                              Funcionario avaliador, TipoAvaliacaoDesempenho tipo) {
        this(ciclo, funcionario, tipo);
        this.avaliador = avaliador;
    }

    // Métodos auxiliares
    public boolean isAutoavaliacao() {
        return tipo == TipoAvaliacaoDesempenho.AUTOAVALIACAO;
    }

    public boolean isAvaliacaoGestor() {
        return tipo == TipoAvaliacaoDesempenho.AVALIACAO_GESTOR;
    }

    public boolean isAvaliacao360() {
        return tipo == TipoAvaliacaoDesempenho.AVALIACAO_360;
    }

    public boolean isConcluida() {
        return status == StatusAvaliacaoDesempenho.CONCLUIDA;
    }

    public boolean isAprovada() {
        return status == StatusAvaliacaoDesempenho.APROVADA;
    }

    public void calcularPercentualConclusao() {
        if (respostas == null || respostas.isEmpty()) {
            this.percentualConclusao = BigDecimal.ZERO;
            return;
        }

        long totalQuestoes = 0;
        long respondidas = 0;
        
        // TODO: Implementar lógica baseada no formulário associado
        // Por enquanto, calcula baseado nas respostas existentes
        for (RespostaAvaliacao resposta : respostas) {
            if (resposta.getResposta() != null && !resposta.getResposta().isEmpty()) {
                respondidas++;
            }
            totalQuestoes++;
        }

        if (totalQuestoes > 0) {
            this.percentualConclusao = BigDecimal.valueOf(respondidas)
                    .divide(BigDecimal.valueOf(totalQuestoes), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}