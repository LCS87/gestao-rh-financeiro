package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.StatusCicloAvaliacao;
import br.com.callcenter.modulorh.domain.enums.TipoCicloAvaliacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ciclos_avaliacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CicloAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoCicloAvaliacao tipo = TipoCicloAvaliacao.TRIMESTRAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusCicloAvaliacao status = StatusCicloAvaliacao.PLANEJADO;

    @Column(name = "ano_referencia", nullable = false)
    private Integer anoReferencia;

    @Column(name = "periodo_referencia")
    private Integer periodoReferencia; // 1-4 para trimestral, 1-2 para semestral

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "data_inicio_autoavaliacao")
    private LocalDate dataInicioAutoavaliacao;

    @Column(name = "data_fim_autoavaliacao")
    private LocalDate dataFimAutoavaliacao;

    @Column(name = "data_inicio_avaliacao_gestor")
    private LocalDate dataInicioAvaliacaoGestor;

    @Column(name = "data_fim_avaliacao_gestor")
    private LocalDate dataFimAvaliacaoGestor;

    @Column(name = "data_inicio_feedback")
    private LocalDate dataInicioFeedback;

    @Column(name = "data_fim_feedback")
    private LocalDate dataFimFeedback;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_autoavaliacao_id")
    private FormularioAvaliacao formularioAutoavaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_avaliacao_gestor_id")
    private FormularioAvaliacao formularioAvaliacaoGestor;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "ciclo", cascade = CascadeType.ALL)
    private List<AvaliacaoDesempenho> avaliacoes = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public CicloAvaliacao(String nome, TipoCicloAvaliacao tipo, Integer anoReferencia, 
                         Integer periodoReferencia, LocalDate dataInicio, LocalDate dataFim) {
        this.nome = nome;
        this.tipo = tipo;
        this.anoReferencia = anoReferencia;
        this.periodoReferencia = periodoReferencia;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusCicloAvaliacao.PLANEJADO;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public boolean isEmAndamento() {
        LocalDate hoje = LocalDate.now();
        return hoje.isAfter(dataInicio) && hoje.isBefore(dataFim);
    }

    public boolean isAutoavaliacaoAtiva() {
        if (dataInicioAutoavaliacao == null || dataFimAutoavaliacao == null) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        return hoje.isAfter(dataInicioAutoavaliacao) && hoje.isBefore(dataFimAutoavaliacao);
    }

    public boolean isAvaliacaoGestorAtiva() {
        if (dataInicioAvaliacaoGestor == null || dataFimAvaliacaoGestor == null) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        return hoje.isAfter(dataInicioAvaliacaoGestor) && hoje.isBefore(dataFimAvaliacaoGestor);
    }

    public boolean isFeedbackAtivo() {
        if (dataInicioFeedback == null || dataFimFeedback == null) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        return hoje.isAfter(dataInicioFeedback) && hoje.isBefore(dataFimFeedback);
    }
}