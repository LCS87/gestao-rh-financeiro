package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.TipoQuestaoAvaliacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "questoes_avaliacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestaoAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_id", nullable = false)
    private FormularioAvaliacao formulario;

    @Column(nullable = false, length = 1000)
    private String enunciado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoQuestaoAvaliacao tipo = TipoQuestaoAvaliacao.ESCALA_LIKERT;

    @Column(nullable = false)
    private Integer ordem;

    @Column(length = 2000)
    private String opcoes; // JSON ou lista separada por pipe para questões de múltipla escolha

    @Column
    private Integer peso = 1; // Peso da questão no cálculo da nota

    @Column(nullable = false)
    private Boolean obrigatoria = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public QuestaoAvaliacao(FormularioAvaliacao formulario, String enunciado, 
                           TipoQuestaoAvaliacao tipo, Integer ordem, 
                           String opcoes, Integer peso) {
        this.formulario = formulario;
        this.enunciado = enunciado;
        this.tipo = tipo;
        this.ordem = ordem;
        this.opcoes = opcoes;
        this.peso = peso;
        this.obrigatoria = true;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public boolean isQuestaoEscala() {
        return tipo == TipoQuestaoAvaliacao.ESCALA_LIKERT || 
               tipo == TipoQuestaoAvaliacao.ESCALA_NUMERICA;
    }

    public boolean isQuestaoTexto() {
        return tipo == TipoQuestaoAvaliacao.TEXTO_LIVRE || 
               tipo == TipoQuestaoAvaliacao.TEXTO_LONGO;
    }

    public boolean isQuestaoMultiplaEscolha() {
        return tipo == TipoQuestaoAvaliacao.MULTIPLA_ESCOLHA || 
               tipo == TipoQuestaoAvaliacao.UNICA_ESCOLHA;
    }

    public String[] getOpcoesArray() {
        if (opcoes == null || opcoes.isEmpty()) {
            return new String[0];
        }
        return opcoes.split("\\|");
    }
}