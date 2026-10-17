package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCicloAvaliacao is a Querydsl query type for CicloAvaliacao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCicloAvaliacao extends EntityPathBase<CicloAvaliacao> {

    private static final long serialVersionUID = -320215284L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCicloAvaliacao cicloAvaliacao = new QCicloAvaliacao("cicloAvaliacao");

    public final NumberPath<Integer> anoReferencia = createNumber("anoReferencia", Integer.class);

    public final BooleanPath ativo = createBoolean("ativo");

    public final ListPath<AvaliacaoDesempenho, QAvaliacaoDesempenho> avaliacoes = this.<AvaliacaoDesempenho, QAvaliacaoDesempenho>createList("avaliacoes", AvaliacaoDesempenho.class, QAvaliacaoDesempenho.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> dataFim = createDate("dataFim", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataFimAutoavaliacao = createDate("dataFimAutoavaliacao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataFimAvaliacaoGestor = createDate("dataFimAvaliacaoGestor", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataFimFeedback = createDate("dataFimFeedback", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicio = createDate("dataInicio", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicioAutoavaliacao = createDate("dataInicioAutoavaliacao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicioAvaliacaoGestor = createDate("dataInicioAvaliacaoGestor", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataInicioFeedback = createDate("dataInicioFeedback", java.time.LocalDate.class);

    public final StringPath descricao = createString("descricao");

    public final QFormularioAvaliacao formularioAutoavaliacao;

    public final QFormularioAvaliacao formularioAvaliacaoGestor;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final NumberPath<Integer> periodoReferencia = createNumber("periodoReferencia", Integer.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.StatusCicloAvaliacao> status = createEnum("status", br.com.callcenter.modulorh.domain.enums.StatusCicloAvaliacao.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoCicloAvaliacao> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoCicloAvaliacao.class);

    public QCicloAvaliacao(String variable) {
        this(CicloAvaliacao.class, forVariable(variable), INITS);
    }

    public QCicloAvaliacao(Path<? extends CicloAvaliacao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCicloAvaliacao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCicloAvaliacao(PathMetadata metadata, PathInits inits) {
        this(CicloAvaliacao.class, metadata, inits);
    }

    public QCicloAvaliacao(Class<? extends CicloAvaliacao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.formularioAutoavaliacao = inits.isInitialized("formularioAutoavaliacao") ? new QFormularioAvaliacao(forProperty("formularioAutoavaliacao"), inits.get("formularioAutoavaliacao")) : null;
        this.formularioAvaliacaoGestor = inits.isInitialized("formularioAvaliacaoGestor") ? new QFormularioAvaliacao(forProperty("formularioAvaliacaoGestor"), inits.get("formularioAvaliacaoGestor")) : null;
    }

}

