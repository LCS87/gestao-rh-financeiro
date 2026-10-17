package br.com.callcenter.modulo_comissoes.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeta is a Querydsl query type for Meta
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeta extends EntityPathBase<Meta> {

    private static final long serialVersionUID = 1526514712L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeta meta = new QMeta("meta");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataConclusao = createDateTime("dataConclusao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final br.com.callcenter.modulorh.domain.entity.QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<MetaFuncionario, QMetaFuncionario> metasFuncionarios = this.<MetaFuncionario, QMetaFuncionario>createList("metasFuncionarios", MetaFuncionario.class, QMetaFuncionario.class, PathInits.DIRECT2);

    public final StringPath periodoReferencia = createString("periodoReferencia");

    public final EnumPath<StatusMeta> status = createEnum("status", StatusMeta.class);

    public final EnumPath<TipoMeta> tipoMeta = createEnum("tipoMeta", TipoMeta.class);

    public final NumberPath<java.math.BigDecimal> valorAlcancado = createNumber("valorAlcancado", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorMeta = createNumber("valorMeta", java.math.BigDecimal.class);

    public QMeta(String variable) {
        this(Meta.class, forVariable(variable), INITS);
    }

    public QMeta(Path<? extends Meta> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeta(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeta(PathMetadata metadata, PathInits inits) {
        this(Meta.class, metadata, inits);
    }

    public QMeta(Class<? extends Meta> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funcionario = inits.isInitialized("funcionario") ? new br.com.callcenter.modulorh.domain.entity.QFuncionario(forProperty("funcionario")) : null;
    }

}

