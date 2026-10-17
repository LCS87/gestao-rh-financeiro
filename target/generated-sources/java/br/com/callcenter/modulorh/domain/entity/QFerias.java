package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFerias is a Querydsl query type for Ferias
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFerias extends EntityPathBase<Ferias> {

    private static final long serialVersionUID = -265540997L;

    public static final QFerias ferias = new QFerias("ferias");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataFim = createDate("dataFim", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicio = createDate("dataInicio", java.time.LocalDate.class);

    public final NumberPath<Integer> dias = createNumber("dias", Integer.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath observacoes = createString("observacoes");

    public final EnumPath<Ferias.StatusFerias> status = createEnum("status", Ferias.StatusFerias.class);

    public final EnumPath<Ferias.TipoFerias> tipo = createEnum("tipo", Ferias.TipoFerias.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QFerias(String variable) {
        super(Ferias.class, forVariable(variable));
    }

    public QFerias(Path<? extends Ferias> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFerias(PathMetadata metadata) {
        super(Ferias.class, metadata);
    }

}

