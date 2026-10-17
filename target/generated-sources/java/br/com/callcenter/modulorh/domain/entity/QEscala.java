package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEscala is a Querydsl query type for Escala
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEscala extends EntityPathBase<Escala> {

    private static final long serialVersionUID = -281695084L;

    public static final QEscala escala = new QEscala("escala");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final NumberPath<Double> horasSemanais = createNumber("horasSemanais", Double.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final EnumPath<Escala.TipoEscala> tipo = createEnum("tipo", Escala.TipoEscala.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QEscala(String variable) {
        super(Escala.class, forVariable(variable));
    }

    public QEscala(Path<? extends Escala> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEscala(PathMetadata metadata) {
        super(Escala.class, metadata);
    }

}

