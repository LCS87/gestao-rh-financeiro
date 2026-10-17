package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBeneficio is a Querydsl query type for Beneficio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBeneficio extends EntityPathBase<Beneficio> {

    private static final long serialVersionUID = -127344967L;

    public static final QBeneficio beneficio = new QBeneficio("beneficio");

    public final BooleanPath ativo = createBoolean("ativo");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final BooleanPath obrigatorio = createBoolean("obrigatorio");

    public final NumberPath<java.math.BigDecimal> percentualDesconto = createNumber("percentualDesconto", java.math.BigDecimal.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoBeneficio> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoBeneficio.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoCobrancaBeneficio> tipoCobranca = createEnum("tipoCobranca", br.com.callcenter.modulorh.domain.enums.TipoCobrancaBeneficio.class);

    public final NumberPath<java.math.BigDecimal> valor = createNumber("valor", java.math.BigDecimal.class);

    public QBeneficio(String variable) {
        super(Beneficio.class, forVariable(variable));
    }

    public QBeneficio(Path<? extends Beneficio> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBeneficio(PathMetadata metadata) {
        super(Beneficio.class, metadata);
    }

}

