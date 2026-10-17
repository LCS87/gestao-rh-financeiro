package br.com.callcenter.modulo_comissoes.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMetaFuncionario is a Querydsl query type for MetaFuncionario
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMetaFuncionario extends EntityPathBase<MetaFuncionario> {

    private static final long serialVersionUID = -1636695389L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMetaFuncionario metaFuncionario = new QMetaFuncionario("metaFuncionario");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataConclusao = createDateTime("dataConclusao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final br.com.callcenter.modulorh.domain.entity.QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QMeta meta;

    public final NumberPath<java.math.BigDecimal> percentualConclusao = createNumber("percentualConclusao", java.math.BigDecimal.class);

    public final StringPath status = createString("status");

    public final NumberPath<java.math.BigDecimal> valorAlcancadoIndividual = createNumber("valorAlcancadoIndividual", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorMetaIndividual = createNumber("valorMetaIndividual", java.math.BigDecimal.class);

    public QMetaFuncionario(String variable) {
        this(MetaFuncionario.class, forVariable(variable), INITS);
    }

    public QMetaFuncionario(Path<? extends MetaFuncionario> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMetaFuncionario(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMetaFuncionario(PathMetadata metadata, PathInits inits) {
        this(MetaFuncionario.class, metadata, inits);
    }

    public QMetaFuncionario(Class<? extends MetaFuncionario> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funcionario = inits.isInitialized("funcionario") ? new br.com.callcenter.modulorh.domain.entity.QFuncionario(forProperty("funcionario")) : null;
        this.meta = inits.isInitialized("meta") ? new QMeta(forProperty("meta"), inits.get("meta")) : null;
    }

}

