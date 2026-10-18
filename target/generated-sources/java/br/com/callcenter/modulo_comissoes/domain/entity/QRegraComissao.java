package br.com.callcenter.modulo_comissoes.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRegraComissao is a Querydsl query type for RegraComissao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRegraComissao extends EntityPathBase<RegraComissao> {

    private static final long serialVersionUID = -1048361018L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRegraComissao regraComissao = new QRegraComissao("regraComissao");

    public final NumberPath<java.math.BigDecimal> aceleradorPercentual = createNumber("aceleradorPercentual", java.math.BigDecimal.class);

    public final BooleanPath aplicarPorDepartamento = createBoolean("aplicarPorDepartamento");

    public final BooleanPath ativo = createBoolean("ativo");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final br.com.callcenter.modulorh.domain.entity.QDepartamento departamento;

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> metaMinimaPercentual = createNumber("metaMinimaPercentual", java.math.BigDecimal.class);

    public final StringPath nome = createString("nome");

    public final NumberPath<java.math.BigDecimal> porcentagem = createNumber("porcentagem", java.math.BigDecimal.class);

    public final EnumPath<TipoComissao> tipoComissao = createEnum("tipoComissao", TipoComissao.class);

    public final NumberPath<java.math.BigDecimal> valorFixo = createNumber("valorFixo", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorMaximo = createNumber("valorMaximo", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorMinimo = createNumber("valorMinimo", java.math.BigDecimal.class);

    public QRegraComissao(String variable) {
        this(RegraComissao.class, forVariable(variable), INITS);
    }

    public QRegraComissao(Path<? extends RegraComissao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRegraComissao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRegraComissao(PathMetadata metadata, PathInits inits) {
        this(RegraComissao.class, metadata, inits);
    }

    public QRegraComissao(Class<? extends RegraComissao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.departamento = inits.isInitialized("departamento") ? new br.com.callcenter.modulorh.domain.entity.QDepartamento(forProperty("departamento")) : null;
    }

}

