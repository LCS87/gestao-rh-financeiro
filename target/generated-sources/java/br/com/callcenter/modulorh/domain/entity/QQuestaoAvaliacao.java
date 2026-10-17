package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuestaoAvaliacao is a Querydsl query type for QuestaoAvaliacao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestaoAvaliacao extends EntityPathBase<QuestaoAvaliacao> {

    private static final long serialVersionUID = 919760572L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuestaoAvaliacao questaoAvaliacao = new QQuestaoAvaliacao("questaoAvaliacao");

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final StringPath enunciado = createString("enunciado");

    public final QFormularioAvaliacao formulario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final BooleanPath obrigatoria = createBoolean("obrigatoria");

    public final StringPath opcoes = createString("opcoes");

    public final NumberPath<Integer> ordem = createNumber("ordem", Integer.class);

    public final NumberPath<Integer> peso = createNumber("peso", Integer.class);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoQuestaoAvaliacao> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoQuestaoAvaliacao.class);

    public QQuestaoAvaliacao(String variable) {
        this(QuestaoAvaliacao.class, forVariable(variable), INITS);
    }

    public QQuestaoAvaliacao(Path<? extends QuestaoAvaliacao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuestaoAvaliacao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuestaoAvaliacao(PathMetadata metadata, PathInits inits) {
        this(QuestaoAvaliacao.class, metadata, inits);
    }

    public QQuestaoAvaliacao(Class<? extends QuestaoAvaliacao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.formulario = inits.isInitialized("formulario") ? new QFormularioAvaliacao(forProperty("formulario"), inits.get("formulario")) : null;
    }

}

