package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostagemMural is a Querydsl query type for PostagemMural
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostagemMural extends EntityPathBase<PostagemMural> {

    private static final long serialVersionUID = 297264692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostagemMural postagemMural = new QPostagemMural("postagemMural");

    public final BooleanPath ativa = createBoolean("ativa");

    public final QFuncionario autor;

    public final ListPath<ComentarioPostagem, QComentarioPostagem> comentarios = this.<ComentarioPostagem, QComentarioPostagem>createList("comentarios", ComentarioPostagem.class, QComentarioPostagem.class, PathInits.DIRECT2);

    public final StringPath conteudo = createString("conteudo");

    public final ListPath<CurtidaPostagem, QCurtidaPostagem> curtidas = this.<CurtidaPostagem, QCurtidaPostagem>createList("curtidas", CurtidaPostagem.class, QCurtidaPostagem.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> dataEdicao = createDateTime("dataEdicao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataPublicacao = createDateTime("dataPublicacao", java.time.LocalDateTime.class);

    public final BooleanPath fixada = createBoolean("fixada");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoPostagem> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoPostagem.class);

    public final StringPath titulo = createString("titulo");

    public final StringPath urlAnexo = createString("urlAnexo");

    public final StringPath urlImagem = createString("urlImagem");

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem> visibilidade = createEnum("visibilidade", br.com.callcenter.modulorh.domain.enums.VisibilidadePostagem.class);

    public QPostagemMural(String variable) {
        this(PostagemMural.class, forVariable(variable), INITS);
    }

    public QPostagemMural(Path<? extends PostagemMural> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostagemMural(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostagemMural(PathMetadata metadata, PathInits inits) {
        this(PostagemMural.class, metadata, inits);
    }

    public QPostagemMural(Class<? extends PostagemMural> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.autor = inits.isInitialized("autor") ? new QFuncionario(forProperty("autor")) : null;
    }

}

