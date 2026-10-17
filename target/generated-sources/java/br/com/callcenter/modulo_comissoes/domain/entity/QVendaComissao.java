package br.com.callcenter.modulo_comissoes.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVendaComissao is a Querydsl query type for VendaComissao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVendaComissao extends EntityPathBase<VendaComissao> {

    private static final long serialVersionUID = 835458399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVendaComissao vendaComissao = new QVendaComissao("vendaComissao");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataPagamento = createDateTime("dataPagamento", java.time.LocalDateTime.class);

    public final br.com.callcenter.modulorh.domain.entity.QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath observacoes = createString("observacoes");

    public final QRegraComissao regraComissao;

    public final EnumPath<StatusPagamento> statusPagamento = createEnum("statusPagamento", StatusPagamento.class);

    public final NumberPath<java.math.BigDecimal> valorComissao = createNumber("valorComissao", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorVenda = createNumber("valorVenda", java.math.BigDecimal.class);

    public final br.com.callcenter.modulorh.domain.entity.QVenda venda;

    public QVendaComissao(String variable) {
        this(VendaComissao.class, forVariable(variable), INITS);
    }

    public QVendaComissao(Path<? extends VendaComissao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVendaComissao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVendaComissao(PathMetadata metadata, PathInits inits) {
        this(VendaComissao.class, metadata, inits);
    }

    public QVendaComissao(Class<? extends VendaComissao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funcionario = inits.isInitialized("funcionario") ? new br.com.callcenter.modulorh.domain.entity.QFuncionario(forProperty("funcionario")) : null;
        this.regraComissao = inits.isInitialized("regraComissao") ? new QRegraComissao(forProperty("regraComissao"), inits.get("regraComissao")) : null;
        this.venda = inits.isInitialized("venda") ? new br.com.callcenter.modulorh.domain.entity.QVenda(forProperty("venda"), inits.get("venda")) : null;
    }

}

