package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "respostas_avaliacao", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"avaliacao_id", "questao_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    private AvaliacaoDesempenho avaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private QuestaoAvaliacao questao;

    @Column(length = 2000)
    private String resposta; // Para questões de texto ou opção selecionada

    @Column(precision = 5, scale = 2)
    private BigDecimal nota; // Para questões com pontuação

    @Column(length = 1000)
    private String justificativa; // Justificativa para a resposta

    @Column(name = "data_resposta", nullable = false)
    private LocalDateTime dataResposta = LocalDateTime.now();

    @Column(name = "data_edicao")
    private LocalDateTime dataEdicao;

    @PreUpdate
    public void preUpdate() {
        this.dataEdicao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public RespostaAvaliacao(AvaliacaoDesempenho avaliacao, QuestaoAvaliacao questao, String resposta) {
        this.avaliacao = avaliacao;
        this.questao = questao;
        this.resposta = resposta;
        this.dataResposta = LocalDateTime.now();
    }

    public RespostaAvaliacao(AvaliacaoDesempenho avaliacao, QuestaoAvaliacao questao, 
                            BigDecimal nota, String justificativa) {
        this.avaliacao = avaliacao;
        this.questao = questao;
        this.nota = nota;
        this.justificativa = justificativa;
        this.dataResposta = LocalDateTime.now();
    }

    // Métodos auxiliares
    public boolean isQuestaoNumerica() {
        return questao.getTipo().isQuestaoNumerica();
    }

    public boolean isQuestaoTexto() {
        return questao.getTipo().isQuestaoTexto();
    }

    public BigDecimal calcularNotaPonderada() {
        if (nota == null || questao.getPeso() == null) {
            return BigDecimal.ZERO;
        }
        return nota.multiply(BigDecimal.valueOf(questao.getPeso()));
    }
}