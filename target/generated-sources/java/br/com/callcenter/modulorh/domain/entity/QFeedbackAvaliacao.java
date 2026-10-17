package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFeedbackAvaliacao is a Querydsl query type for FeedbackAvaliacao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeedbackAvaliacao extends EntityPathBase<FeedbackAvaliacao> {

    private static final long serialVersionUID = -1104710623L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFeedbackAvaliacao feedbackAvaliacao = new QFeedbackAvaliacao("feedbackAvaliacao");

    public final QAvaliacaoDesempenho avaliacao;

    public final StringPath comentariosFuncionario = createString("comentariosFuncionario");

    public final StringPath comentariosGestor = createString("comentariosGestor");

    public final DateTimePath<java.time.LocalDateTime> dataAgendamento = createDateTime("dataAgendamento", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataFeedbackConcluido = createDateTime("dataFeedbackConcluido", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataFeedbackFuncionario = createDateTime("dataFeedbackFuncionario", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataFeedbackGestor = createDateTime("dataFeedbackGestor", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath metasFuturas = createString("metasFuturas");

    public final StringPath pontosFortes = createString("pontosFortes");

    public final StringPath pontosMelhoria = createString("pontosMelhoria");

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.StatusFeedbackAvaliacao> status = createEnum("status", br.com.callcenter.modulorh.domain.enums.StatusFeedbackAvaliacao.class);

    public QFeedbackAvaliacao(String variable) {
        this(FeedbackAvaliacao.class, forVariable(variable), INITS);
    }

    public QFeedbackAvaliacao(Path<? extends FeedbackAvaliacao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFeedbackAvaliacao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFeedbackAvaliacao(PathMetadata metadata, PathInits inits) {
        this(FeedbackAvaliacao.class, metadata, inits);
    }

    public QFeedbackAvaliacao(Class<? extends FeedbackAvaliacao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.avaliacao = inits.isInitialized("avaliacao") ? new QAvaliacaoDesempenho(forProperty("avaliacao"), inits.get("avaliacao")) : null;
    }

}

