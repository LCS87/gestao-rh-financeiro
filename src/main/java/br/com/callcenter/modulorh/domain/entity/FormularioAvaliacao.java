package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.TipoFormularioAvaliacao;
import br.com.callcenter.modulorh.domain.enums.TipoQuestaoAvaliacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "formularios_avaliacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoFormularioAvaliacao tipo = TipoFormularioAvaliacao.AUTOAVALIACAO;

    @Column(name = "versao", nullable = false)
    private Integer versao = 1;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_base_id")
    private FormularioAvaliacao formularioBase; // Para versionamento

    @OneToMany(mappedBy = "formulario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<QuestaoAvaliacao> questoes = new ArrayList<>();

    @OneToMany(mappedBy = "formularioAutoavaliacao")
    private List<CicloAvaliacao> ciclosAutoavaliacao = new ArrayList<>();

    @OneToMany(mappedBy = "formularioAvaliacaoGestor")
    private List<CicloAvaliacao> ciclosAvaliacaoGestor = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Construtores auxiliares
    public FormularioAvaliacao(String nome, TipoFormularioAvaliacao tipo) {
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
    }

    public FormularioAvaliacao(String nome, String descricao, TipoFormularioAvaliacao tipo) {
        this(nome, tipo);
        this.descricao = descricao;
    }

    // Métodos auxiliares
    public void adicionarQuestao(String enunciado, TipoQuestaoAvaliacao tipo, Integer ordem, 
                                String opcoes, Integer peso) {
        QuestaoAvaliacao questao = new QuestaoAvaliacao(this, enunciado, tipo, ordem, opcoes, peso);
        this.questoes.add(questao);
    }

    public FormularioAvaliacao criarNovaVersao() {
        FormularioAvaliacao novaVersao = new FormularioAvaliacao();
        novaVersao.setNome(this.nome + " (v" + (this.versao + 1) + ")");
        novaVersao.setDescricao(this.descricao);
        novaVersao.setTipo(this.tipo);
        novaVersao.setVersao(this.versao + 1);
        novaVersao.setFormularioBase(this);
        novaVersao.setAtivo(true);
        
        // Copiar questões (serão persistidas quando o formulário for salvo)
        for (QuestaoAvaliacao questao : this.questoes) {
            QuestaoAvaliacao novaQuestao = new QuestaoAvaliacao();
            novaQuestao.setFormulario(novaVersao);
            novaQuestao.setEnunciado(questao.getEnunciado());
            novaQuestao.setTipo(questao.getTipo());
            novaQuestao.setOrdem(questao.getOrdem());
            novaQuestao.setOpcoes(questao.getOpcoes());
            novaQuestao.setPeso(questao.getPeso());
            novaQuestao.setObrigatoria(questao.getObrigatoria());
            novaVersao.getQuestoes().add(novaQuestao);
        }
        
        return novaVersao;
    }
}