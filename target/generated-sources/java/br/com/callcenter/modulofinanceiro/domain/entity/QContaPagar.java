package br.com.callcenter.modulofinanceiro.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QContaPagar is a Querydsl query type for ContaPagar
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContaPagar extends EntityPathBase<ContaPagar> {

    private static final long serialVersionUID = -2022844715L;

    public static final QContaPagar contaPagar = new QContaPagar("contaPagar");

    public final EnumPath<ContaPagar.CategoriaConta> categoria = createEnum("categoria", ContaPagar.CategoriaConta.class);

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataPagamento = createDate("dataPagamento", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataVencimento = createDate("dataVencimento", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final StringPath fornecedor = createString("fornecedor");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath numeroDocumento = createString("numeroDocumento");

    public final StringPath observacoes = createString("observacoes");

    public final EnumPath<ContaPagar.StatusConta> status = createEnum("status", ContaPagar.StatusConta.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> valorPago = createNumber("valorPago", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorTotal = createNumber("valorTotal", java.math.BigDecimal.class);

    public QContaPagar(String variable) {
        super(ContaPagar.class, forVariable(variable));
    }

    public QContaPagar(Path<? extends ContaPagar> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContaPagar(PathMetadata metadata) {
        super(ContaPagar.class, metadata);
    }

}

