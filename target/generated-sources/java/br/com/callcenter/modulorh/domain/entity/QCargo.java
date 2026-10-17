package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCargo is a Querydsl query type for Cargo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCargo extends EntityPathBase<Cargo> {

    private static final long serialVersionUID = -1812570935L;

    public static final QCargo cargo = new QCargo("cargo");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final NumberPath<Double> salarioBase = createNumber("salarioBase", Double.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QCargo(String variable) {
        super(Cargo.class, forVariable(variable));
    }

    public QCargo(Path<? extends Cargo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCargo(PathMetadata metadata) {
        super(Cargo.class, metadata);
    }

}

