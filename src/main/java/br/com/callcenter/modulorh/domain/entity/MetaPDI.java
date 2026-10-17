package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.StatusMetaPDI;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "metas_pdi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaPDI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_desenvolvimento_id", nullable = false)
    private PlanoDesenvolvimentoIndividual planoDesenvolvimento;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(length = 2000)
    private String acoesEspecificas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusMetaPDI status = StatusMetaPDI.PLANEJADA;

    @Column(nullable = false)
    private Integer peso = 1; // Peso relativo da meta no plano (1-10)

    @Column(name = "percentual_conclusao", precision = 5, scale = 2)
    private Double percentualConclusao = 0.0;

    @Column(name = "data_prevista_conclusao", nullable = false)
    private LocalDate dataPrevistaConclusao;

    @Column(name = "data_real_conclusao")
    private LocalDate dataRealConclusao;

    @Column(name = "evidencias_conclusao", length = 1000)
    private String evidenciasConclusao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public MetaPDI(PlanoDesenvolvimentoIndividual planoDesenvolvimento, String descricao,
                  LocalDate dataPrevistaConclusao, Integer peso) {
        this.planoDesenvolvimento = planoDesenvolvimento;
        this.descricao = descricao;
        this.dataPrevistaConclusao = dataPrevistaConclusao;
        this.peso = peso != null ? peso : 1;
        this.status = StatusMetaPDI.PLANEJADA;
        this.percentualConclusao = 0.0;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public boolean isConcluida() {
        return status == StatusMetaPDI.CONCLUIDA;
    }

    public boolean isEmAndamento() {
        return status == StatusMetaPDI.EM_ANDAMENTO;
    }

    public boolean isAtrasada() {
        LocalDate hoje = LocalDate.now();
        return !isConcluida() && hoje.isAfter(dataPrevistaConclusao);
    }

    public boolean podeMarcarComoConcluida() {
        return percentualConclusao >= 100.0 && !isConcluida();
    }

    public void atualizarStatusPorPercentual() {
        if (percentualConclusao == null) {
            percentualConclusao = 0.0;
        }
        
        if (percentualConclusao >= 100.0) {
            status = StatusMetaPDI.CONCLUIDA;
            if (dataRealConclusao == null) {
                dataRealConclusao = LocalDate.now();
            }
        } else if (percentualConclusao > 0.0) {
            status = StatusMetaPDI.EM_ANDAMENTO;
        } else {
            status = StatusMetaPDI.PLANEJADA;
        }
    }
}