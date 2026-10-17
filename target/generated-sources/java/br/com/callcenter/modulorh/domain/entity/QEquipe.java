package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEquipe is a Querydsl query type for Equipe
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEquipe extends EntityPathBase<Equipe> {

    private static final long serialVersionUID = -282998072L;

    public static final QEquipe equipe = new QEquipe("equipe");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> departamentoId = createComparable("departamentoId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final ComparablePath<java.util.UUID> supervisorId = createComparable("supervisorId", java.util.UUID.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QEquipe(String variable) {
        super(Equipe.class, forVariable(variable));
    }

    public QEquipe(Path<? extends Equipe> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEquipe(PathMetadata metadata) {
        super(Equipe.class, metadata);
    }

}

