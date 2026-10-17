package br.com.callcenter.modulo_comissoes.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComissaoPaga is a Querydsl query type for ComissaoPaga
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComissaoPaga extends EntityPathBase<ComissaoPaga> {

    private static final long serialVersionUID = 2078209108L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComissaoPaga comissaoPaga = new QComissaoPaga("comissaoPaga");

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataPagamento = createDateTime("dataPagamento", java.time.LocalDateTime.class);

    public final br.com.callcenter.modulorh.domain.entity.QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath mesReferencia = createString("mesReferencia");

    public final StringPath observacoes = createString("observacoes");

    public final StringPath status = createString("status");

    public final NumberPath<java.math.BigDecimal> valorComissao = createNumber("valorComissao", java.math.BigDecimal.class);

    public final QVendaComissao vendaComissao;

    public QComissaoPaga(String variable) {
        this(ComissaoPaga.class, forVariable(variable), INITS);
    }

    public QComissaoPaga(Path<? extends ComissaoPaga> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComissaoPaga(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComissaoPaga(PathMetadata metadata, PathInits inits) {
        this(ComissaoPaga.class, metadata, inits);
    }

    public QComissaoPaga(Class<? extends ComissaoPaga> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funcionario = inits.isInitialized("funcionario") ? new br.com.callcenter.modulorh.domain.entity.QFuncionario(forProperty("funcionario")) : null;
        this.vendaComissao = inits.isInitialized("vendaComissao") ? new QVendaComissao(forProperty("vendaComissao"), inits.get("vendaComissao")) : null;
    }

}

