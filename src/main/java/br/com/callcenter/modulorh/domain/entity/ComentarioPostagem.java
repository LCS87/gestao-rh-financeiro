package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comentarios_postagem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioPostagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postagem_id", nullable = false)
    private PostagemMural postagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Funcionario autor;

    @Column(nullable = false, length = 1000)
    private String conteudo;

    @Column(name = "data_comentario", nullable = false)
    private LocalDateTime dataComentario = LocalDateTime.now();

    @Column(name = "data_edicao")
    private LocalDateTime dataEdicao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_pai_id")
    private ComentarioPostagem comentarioPai; // Para respostas a comentários

    @PreUpdate
    public void preUpdate() {
        this.dataEdicao = LocalDateTime.now();
    }

    // Construtor auxiliar
    public ComentarioPostagem(PostagemMural postagem, Funcionario autor, String conteudo) {
        this.postagem = postagem;
        this.autor = autor;
        this.conteudo = conteudo;
        this.ativo = true;
        this.dataComentario = LocalDateTime.now();
    }

    public ComentarioPostagem(PostagemMural postagem, Funcionario autor, String conteudo, 
                             ComentarioPostagem comentarioPai) {
        this(postagem, autor, conteudo);
        this.comentarioPai = comentarioPai;
    }
}