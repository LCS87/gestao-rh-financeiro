package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAvaliacaoDesempenho is a Querydsl query type for AvaliacaoDesempenho
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAvaliacaoDesempenho extends EntityPathBase<AvaliacaoDesempenho> {

    private static final long serialVersionUID = 1107441580L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAvaliacaoDesempenho avaliacaoDesempenho = new QAvaliacaoDesempenho("avaliacaoDesempenho");

    public final QFuncionario avaliador;

    public final QCicloAvaliacao ciclo;

    public final StringPath comentariosAvaliador = createString("comentariosAvaliador");

    public final StringPath comentariosFuncionario = createString("comentariosFuncionario");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataConclusao = createDateTime("dataConclusao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataInicio = createDateTime("dataInicio", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataUltimaAtualizacao = createDateTime("dataUltimaAtualizacao", java.time.LocalDateTime.class);

    public final QFeedbackAvaliacao feedback;

    public final QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> notaFinal = createNumber("notaFinal", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> percentualConclusao = createNumber("percentualConclusao", java.math.BigDecimal.class);

    public final QPlanoDesenvolvimentoIndividual planoDesenvolvimento;

    public final ListPath<RespostaAvaliacao, QRespostaAvaliacao> respostas = this.<RespostaAvaliacao, QRespostaAvaliacao>createList("respostas", RespostaAvaliacao.class, QRespostaAvaliacao.class, PathInits.DIRECT2);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.StatusAvaliacaoDesempenho> status = createEnum("status", br.com.callcenter.modulorh.domain.enums.StatusAvaliacaoDesempenho.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoAvaliacaoDesempenho> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoAvaliacaoDesempenho.class);

    public QAvaliacaoDesempenho(String variable) {
        this(AvaliacaoDesempenho.class, forVariable(variable), INITS);
    }

    public QAvaliacaoDesempenho(Path<? extends AvaliacaoDesempenho> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAvaliacaoDesempenho(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAvaliacaoDesempenho(PathMetadata metadata, PathInits inits) {
        this(AvaliacaoDesempenho.class, metadata, inits);
    }

    public QAvaliacaoDesempenho(Class<? extends AvaliacaoDesempenho> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.avaliador = inits.isInitialized("avaliador") ? new QFuncionario(forProperty("avaliador")) : null;
        this.ciclo = inits.isInitialized("ciclo") ? new QCicloAvaliacao(forProperty("ciclo"), inits.get("ciclo")) : null;
        this.feedback = inits.isInitialized("feedback") ? new QFeedbackAvaliacao(forProperty("feedback"), inits.get("feedback")) : null;
        this.funcionario = inits.isInitialized("funcionario") ? new QFuncionario(forProperty("funcionario")) : null;
        this.planoDesenvolvimento = inits.isInitialized("planoDesenvolvimento") ? new QPlanoDesenvolvimentoIndividual(forProperty("planoDesenvolvimento"), inits.get("planoDesenvolvimento")) : null;
    }

}

