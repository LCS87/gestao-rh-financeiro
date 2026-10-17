package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDepartamento is a Querydsl query type for Departamento
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDepartamento extends EntityPathBase<Departamento> {

    private static final long serialVersionUID = 2079940183L;

    public static final QDepartamento departamento = new QDepartamento("departamento");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final ComparablePath<java.util.UUID> responsavelId = createComparable("responsavelId", java.util.UUID.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QDepartamento(String variable) {
        super(Departamento.class, forVariable(variable));
    }

    public QDepartamento(Path<? extends Departamento> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDepartamento(PathMetadata metadata) {
        super(Departamento.class, metadata);
    }

}

