package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCurtidaPostagem is a Querydsl query type for CurtidaPostagem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCurtidaPostagem extends EntityPathBase<CurtidaPostagem> {

    private static final long serialVersionUID = 132005581L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCurtidaPostagem curtidaPostagem = new QCurtidaPostagem("curtidaPostagem");

    public final DateTimePath<java.time.LocalDateTime> dataCurtida = createDateTime("dataCurtida", java.time.LocalDateTime.class);

    public final QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QPostagemMural postagem;

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoReacao> tipoReacao = createEnum("tipoReacao", br.com.callcenter.modulorh.domain.enums.TipoReacao.class);

    public QCurtidaPostagem(String variable) {
        this(CurtidaPostagem.class, forVariable(variable), INITS);
    }

    public QCurtidaPostagem(Path<? extends CurtidaPostagem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCurtidaPostagem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCurtidaPostagem(PathMetadata metadata, PathInits inits) {
        this(CurtidaPostagem.class, metadata, inits);
    }

    public QCurtidaPostagem(Class<? extends CurtidaPostagem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funcionario = inits.isInitialized("funcionario") ? new QFuncionario(forProperty("funcionario")) : null;
        this.postagem = inits.isInitialized("postagem") ? new QPostagemMural(forProperty("postagem"), inits.get("postagem")) : null;
    }

}

