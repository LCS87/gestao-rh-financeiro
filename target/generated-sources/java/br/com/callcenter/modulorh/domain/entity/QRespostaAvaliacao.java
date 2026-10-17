package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRespostaAvaliacao is a Querydsl query type for RespostaAvaliacao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRespostaAvaliacao extends EntityPathBase<RespostaAvaliacao> {

    private static final long serialVersionUID = 100239365L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRespostaAvaliacao respostaAvaliacao = new QRespostaAvaliacao("respostaAvaliacao");

    public final QAvaliacaoDesempenho avaliacao;

    public final DateTimePath<java.time.LocalDateTime> dataEdicao = createDateTime("dataEdicao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataResposta = createDateTime("dataResposta", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath justificativa = createString("justificativa");

    public final NumberPath<java.math.BigDecimal> nota = createNumber("nota", java.math.BigDecimal.class);

    public final QQuestaoAvaliacao questao;

    public final StringPath resposta = createString("resposta");

    public QRespostaAvaliacao(String variable) {
        this(RespostaAvaliacao.class, forVariable(variable), INITS);
    }

    public QRespostaAvaliacao(Path<? extends RespostaAvaliacao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRespostaAvaliacao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRespostaAvaliacao(PathMetadata metadata, PathInits inits) {
        this(RespostaAvaliacao.class, metadata, inits);
    }

    public QRespostaAvaliacao(Class<? extends RespostaAvaliacao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.avaliacao = inits.isInitialized("avaliacao") ? new QAvaliacaoDesempenho(forProperty("avaliacao"), inits.get("avaliacao")) : null;
        this.questao = inits.isInitialized("questao") ? new QQuestaoAvaliacao(forProperty("questao"), inits.get("questao")) : null;
    }

}

