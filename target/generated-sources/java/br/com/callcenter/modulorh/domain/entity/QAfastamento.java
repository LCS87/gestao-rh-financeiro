package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAfastamento is a Querydsl query type for Afastamento
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAfastamento extends EntityPathBase<Afastamento> {

    private static final long serialVersionUID = -547143462L;

    public static final QAfastamento afastamento = new QAfastamento("afastamento");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataFim = createDate("dataFim", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicio = createDate("dataInicio", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath motivo = createString("motivo");

    public final StringPath observacoes = createString("observacoes");

    public final EnumPath<Afastamento.TipoAfastamento> tipo = createEnum("tipo", Afastamento.TipoAfastamento.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QAfastamento(String variable) {
        super(Afastamento.class, forVariable(variable));
    }

    public QAfastamento(Path<? extends Afastamento> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAfastamento(PathMetadata metadata) {
        super(Afastamento.class, metadata);
    }

}

