package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMetaPDI is a Querydsl query type for MetaPDI
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMetaPDI extends EntityPathBase<MetaPDI> {

    private static final long serialVersionUID = -2017654147L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMetaPDI metaPDI = new QMetaPDI("metaPDI");

    public final StringPath acoesEspecificas = createString("acoesEspecificas");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> dataPrevistaConclusao = createDate("dataPrevistaConclusao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataRealConclusao = createDate("dataRealConclusao", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final StringPath evidenciasConclusao = createString("evidenciasConclusao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Double> percentualConclusao = createNumber("percentualConclusao", Double.class);

    public final NumberPath<Integer> peso = createNumber("peso", Integer.class);

    public final QPlanoDesenvolvimentoIndividual planoDesenvolvimento;

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.StatusMetaPDI> status = createEnum("status", br.com.callcenter.modulorh.domain.enums.StatusMetaPDI.class);

    public QMetaPDI(String variable) {
        this(MetaPDI.class, forVariable(variable), INITS);
    }

    public QMetaPDI(Path<? extends MetaPDI> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMetaPDI(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMetaPDI(PathMetadata metadata, PathInits inits) {
        this(MetaPDI.class, metadata, inits);
    }

    public QMetaPDI(Class<? extends MetaPDI> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.planoDesenvolvimento = inits.isInitialized("planoDesenvolvimento") ? new QPlanoDesenvolvimentoIndividual(forProperty("planoDesenvolvimento"), inits.get("planoDesenvolvimento")) : null;
    }

}

