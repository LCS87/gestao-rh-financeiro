package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.StatusPDI;
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
@Table(name = "planos_desenvolvimento_individual")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanoDesenvolvimentoIndividual {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    private AvaliacaoDesempenho avaliacao;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusPDI status = StatusPDI.RASCUNHO;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_termino_previsto", nullable = false)
    private LocalDate dataTerminoPrevisto;

    @Column(name = "data_termino_real")
    private LocalDate dataTerminoReal;

    @Column(name = "percentual_conclusao", precision = 5, scale = 2)
    private Double percentualConclusao = 0.0;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovador_id")
    private Funcionario aprovador;

    @OneToMany(mappedBy = "planoDesenvolvimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetaPDI> metas = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public PlanoDesenvolvimentoIndividual(AvaliacaoDesempenho avaliacao, String titulo, 
                                         LocalDate dataInicio, LocalDate dataTerminoPrevisto) {
        this.avaliacao = avaliacao;
        this.titulo = titulo;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevisto = dataTerminoPrevisto;
        this.status = StatusPDI.RASCUNHO;
        this.percentualConclusao = 0.0;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public void adicionarMeta(String descricao, LocalDate dataPrevistaConclusao, Integer peso) {
        MetaPDI meta = new MetaPDI(this, descricao, dataPrevistaConclusao, peso);
        this.metas.add(meta);
    }

    public void calcularPercentualConclusao() {
        if (metas == null || metas.isEmpty()) {
            this.percentualConclusao = 0.0;
            return;
        }

        double totalPeso = 0.0;
        double concluidoPeso = 0.0;

        for (MetaPDI meta : metas) {
            totalPeso += meta.getPeso();
            if (meta.isConcluida()) {
                concluidoPeso += meta.getPeso() * (meta.getPercentualConclusao() / 100.0);
            }
        }

        if (totalPeso > 0) {
            this.percentualConclusao = (concluidoPeso / totalPeso) * 100.0;
        }
    }

    public boolean isConcluido() {
        return status == StatusPDI.CONCLUIDO;
    }

    public boolean isEmAndamento() {
        return status == StatusPDI.EM_ANDAMENTO;
    }

    public boolean isAtrasado() {
        LocalDate hoje = LocalDate.now();
        return !isConcluido() && hoje.isAfter(dataTerminoPrevisto);
    }
}