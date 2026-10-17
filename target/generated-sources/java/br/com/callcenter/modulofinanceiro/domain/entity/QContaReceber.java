package br.com.callcenter.modulofinanceiro.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QContaReceber is a Querydsl query type for ContaReceber
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContaReceber extends EntityPathBase<ContaReceber> {

    private static final long serialVersionUID = -742616408L;

    public static final QContaReceber contaReceber = new QContaReceber("contaReceber");

    public final EnumPath<ContaReceber.CategoriaReceita> categoria = createEnum("categoria", ContaReceber.CategoriaReceita.class);

    public final StringPath cliente = createString("cliente");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataRecebimento = createDate("dataRecebimento", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataVencimento = createDate("dataVencimento", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath numeroDocumento = createString("numeroDocumento");

    public final StringPath observacoes = createString("observacoes");

    public final EnumPath<ContaReceber.StatusConta> status = createEnum("status", ContaReceber.StatusConta.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> valorRecebido = createNumber("valorRecebido", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorTotal = createNumber("valorTotal", java.math.BigDecimal.class);

    public QContaReceber(String variable) {
        super(ContaReceber.class, forVariable(variable));
    }

    public QContaReceber(Path<? extends ContaReceber> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContaReceber(PathMetadata metadata) {
        super(ContaReceber.class, metadata);
    }

}

