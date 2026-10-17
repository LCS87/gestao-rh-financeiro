package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlanoDesenvolvimentoIndividual is a Querydsl query type for PlanoDesenvolvimentoIndividual
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlanoDesenvolvimentoIndividual extends EntityPathBase<PlanoDesenvolvimentoIndividual> {

    private static final long serialVersionUID = -467590836L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlanoDesenvolvimentoIndividual planoDesenvolvimentoIndividual = new QPlanoDesenvolvimentoIndividual("planoDesenvolvimentoIndividual");

    public final QFuncionario aprovador;

    public final QAvaliacaoDesempenho avaliacao;

    public final DateTimePath<java.time.LocalDateTime> dataAprovacao = createDateTime("dataAprovacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> dataInicio = createDate("dataInicio", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataTerminoPrevisto = createDate("dataTerminoPrevisto", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataTerminoReal = createDate("dataTerminoReal", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<MetaPDI, QMetaPDI> metas = this.<MetaPDI, QMetaPDI>createList("metas", MetaPDI.class, QMetaPDI.class, PathInits.DIRECT2);

    public final NumberPath<Double> percentualConclusao = createNumber("percentualConclusao", Double.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.StatusPDI> status = createEnum("status", br.com.callcenter.modulorh.domain.enums.StatusPDI.class);

    public final StringPath titulo = createString("titulo");

    public QPlanoDesenvolvimentoIndividual(String variable) {
        this(PlanoDesenvolvimentoIndividual.class, forVariable(variable), INITS);
    }

    public QPlanoDesenvolvimentoIndividual(Path<? extends PlanoDesenvolvimentoIndividual> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlanoDesenvolvimentoIndividual(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlanoDesenvolvimentoIndividual(PathMetadata metadata, PathInits inits) {
        this(PlanoDesenvolvimentoIndividual.class, metadata, inits);
    }

    public QPlanoDesenvolvimentoIndividual(Class<? extends PlanoDesenvolvimentoIndividual> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.aprovador = inits.isInitialized("aprovador") ? new QFuncionario(forProperty("aprovador")) : null;
        this.avaliacao = inits.isInitialized("avaliacao") ? new QAvaliacaoDesempenho(forProperty("avaliacao"), inits.get("avaliacao")) : null;
    }

}

