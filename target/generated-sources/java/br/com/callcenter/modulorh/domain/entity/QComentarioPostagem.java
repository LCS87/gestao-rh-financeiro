package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComentarioPostagem is a Querydsl query type for ComentarioPostagem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComentarioPostagem extends EntityPathBase<ComentarioPostagem> {

    private static final long serialVersionUID = 2073015042L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComentarioPostagem comentarioPostagem = new QComentarioPostagem("comentarioPostagem");

    public final BooleanPath ativo = createBoolean("ativo");

    public final QFuncionario autor;

    public final QComentarioPostagem comentarioPai;

    public final StringPath conteudo = createString("conteudo");

    public final DateTimePath<java.time.LocalDateTime> dataComentario = createDateTime("dataComentario", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataEdicao = createDateTime("dataEdicao", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QPostagemMural postagem;

    public QComentarioPostagem(String variable) {
        this(ComentarioPostagem.class, forVariable(variable), INITS);
    }

    public QComentarioPostagem(Path<? extends ComentarioPostagem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComentarioPostagem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComentarioPostagem(PathMetadata metadata, PathInits inits) {
        this(ComentarioPostagem.class, metadata, inits);
    }

    public QComentarioPostagem(Class<? extends ComentarioPostagem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.autor = inits.isInitialized("autor") ? new QFuncionario(forProperty("autor")) : null;
        this.comentarioPai = inits.isInitialized("comentarioPai") ? new QComentarioPostagem(forProperty("comentarioPai"), inits.get("comentarioPai")) : null;
        this.postagem = inits.isInitialized("postagem") ? new QPostagemMural(forProperty("postagem"), inits.get("postagem")) : null;
    }

}

