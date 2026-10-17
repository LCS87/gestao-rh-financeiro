package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVenda is a Querydsl query type for Venda
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVenda extends EntityPathBase<Venda> {

    private static final long serialVersionUID = -1794908823L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVenda venda = new QVenda("venda");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataVenda = createDateTime("dataVenda", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> valor = createNumber("valor", java.math.BigDecimal.class);

    public final QFuncionario vendedor;

    public QVenda(String variable) {
        this(Venda.class, forVariable(variable), INITS);
    }

    public QVenda(Path<? extends Venda> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVenda(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVenda(PathMetadata metadata, PathInits inits) {
        this(Venda.class, metadata, inits);
    }

    public QVenda(Class<? extends Venda> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.vendedor = inits.isInitialized("vendedor") ? new QFuncionario(forProperty("vendedor")) : null;
    }

}

