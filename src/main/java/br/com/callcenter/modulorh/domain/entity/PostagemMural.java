package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.TipoPostagem;
import br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "postagens_mural")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostagemMural {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Funcionario autor;

    @Column(nullable = false, length = 500)
    private String conteudo;

    @Column(length = 100)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoPostagem tipo = TipoPostagem.PUBLICACAO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VisibilidadePostagem visibilidade = VisibilidadePostagem.PUBLICO;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao = LocalDateTime.now();

    @Column(name = "data_edicao")
    private LocalDateTime dataEdicao;

    @Column(nullable = false)
    private Boolean fixada = false;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "url_imagem")
    private String urlImagem;

    @Column(name = "url_anexo")
    private String urlAnexo;

    @OneToMany(mappedBy = "postagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComentarioPostagem> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "postagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CurtidaPostagem> curtidas = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.dataEdicao = LocalDateTime.now();
    }

    // Construtores auxiliares
    public PostagemMural(Funcionario autor, String conteudo, TipoPostagem tipo) {
        this.autor = autor;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.ativa = true;
        this.dataPublicacao = LocalDateTime.now();
    }

    public PostagemMural(Funcionario autor, String titulo, String conteudo, TipoPostagem tipo) {
        this.autor = autor;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.ativa = true;
        this.dataPublicacao = LocalDateTime.now();
    }
}