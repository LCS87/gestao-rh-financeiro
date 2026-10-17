package br.com.callcenter.modulorelatorios.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFluxoCaixa is a Querydsl query type for FluxoCaixa
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFluxoCaixa extends EntityPathBase<FluxoCaixa> {

    private static final long serialVersionUID = 1165138937L;

    public static final QFluxoCaixa fluxoCaixa = new QFluxoCaixa("fluxoCaixa");

    public final StringPath categoria = createString("categoria");

    public final StringPath contaBancaria = createString("contaBancaria");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> data = createDate("data", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath periodo = createString("periodo");

    public final NumberPath<Double> saldoAcumulado = createNumber("saldoAcumulado", Double.class);

    public final StringPath tipo = createString("tipo");

    public final NumberPath<Double> valor = createNumber("valor", Double.class);

    public QFluxoCaixa(String variable) {
        super(FluxoCaixa.class, forVariable(variable));
    }

    public QFluxoCaixa(Path<? extends FluxoCaixa> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFluxoCaixa(PathMetadata metadata) {
        super(FluxoCaixa.class, metadata);
    }

}

