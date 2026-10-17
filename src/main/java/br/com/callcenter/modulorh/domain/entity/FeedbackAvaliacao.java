package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.StatusFeedbackAvaliacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedbacks_avaliacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    private AvaliacaoDesempenho avaliacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusFeedbackAvaliacao status = StatusFeedbackAvaliacao.PENDENTE;

    @Column(name = "comentarios_gestor", length = 2000)
    private String comentariosGestor;

    @Column(name = "comentarios_funcionario", length = 2000)
    private String comentariosFuncionario;

    @Column(name = "pontos_fortes", length = 2000)
    private String pontosFortes;

    @Column(name = "pontos_melhoria", length = 2000)
    private String pontosMelhoria;

    @Column(name = "metas_futuras", length = 2000)
    private String metasFuturas;

    @Column(name = "data_feedback_gestor")
    private LocalDateTime dataFeedbackGestor;

    @Column(name = "data_feedback_funcionario")
    private LocalDateTime dataFeedbackFuncionario;

    @Column(name = "data_feedback_concluido")
    private LocalDateTime dataFeedbackConcluido;

    @Column(name = "data_agendamento")
    private LocalDateTime dataAgendamento;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public FeedbackAvaliacao(AvaliacaoDesempenho avaliacao) {
        this.avaliacao = avaliacao;
        this.status = StatusFeedbackAvaliacao.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public boolean isConcluido() {
        return status == StatusFeedbackAvaliacao.CONCLUIDO;
    }

    public boolean isAguardandoGestor() {
        return status == StatusFeedbackAvaliacao.AGUARDANDO_GESTOR;
    }

    public boolean isAguardandoFuncionario() {
        return status == StatusFeedbackAvaliacao.AGUARDANDO_FUNCIONARIO;
    }

    public void iniciarFeedbackGestor() {
        this.status = StatusFeedbackAvaliacao.AGUARDANDO_GESTOR;
    }

    public void completarFeedbackGestor(String comentarios) {
        this.comentariosGestor = comentarios;
        this.dataFeedbackGestor = LocalDateTime.now();
        this.status = StatusFeedbackAvaliacao.AGUARDANDO_FUNCIONARIO;
    }

    public void completarFeedbackFuncionario(String comentarios) {
        this.comentariosFuncionario = comentarios;
        this.dataFeedbackFuncionario = LocalDateTime.now();
        this.status = StatusFeedbackAvaliacao.CONCLUIDO;
        this.dataFeedbackConcluido = LocalDateTime.now();
    }

    public void agendarFeedback(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
        this.status = StatusFeedbackAvaliacao.AGENDADO;
    }
}