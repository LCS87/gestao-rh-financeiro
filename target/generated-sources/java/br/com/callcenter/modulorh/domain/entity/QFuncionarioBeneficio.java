package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFuncionarioBeneficio is a Querydsl query type for FuncionarioBeneficio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFuncionarioBeneficio extends EntityPathBase<FuncionarioBeneficio> {

    private static final long serialVersionUID = 1657753572L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFuncionarioBeneficio funcionarioBeneficio = new QFuncionarioBeneficio("funcionarioBeneficio");

    public final BooleanPath ativo = createBoolean("ativo");

    public final QBeneficio beneficio;

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> dataFim = createDate("dataFim", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicio = createDate("dataInicio", java.time.LocalDate.class);

    public final QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> valorEmpresa = createNumber("valorEmpresa", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valorFuncionario = createNumber("valorFuncionario", java.math.BigDecimal.class);

    public QFuncionarioBeneficio(String variable) {
        this(FuncionarioBeneficio.class, forVariable(variable), INITS);
    }

    public QFuncionarioBeneficio(Path<? extends FuncionarioBeneficio> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFuncionarioBeneficio(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFuncionarioBeneficio(PathMetadata metadata, PathInits inits) {
        this(FuncionarioBeneficio.class, metadata, inits);
    }

    public QFuncionarioBeneficio(Class<? extends FuncionarioBeneficio> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.beneficio = inits.isInitialized("beneficio") ? new QBeneficio(forProperty("beneficio")) : null;
        this.funcionario = inits.isInitialized("funcionario") ? new QFuncionario(forProperty("funcionario")) : null;
    }

}

