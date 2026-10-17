package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFormularioAvaliacao is a Querydsl query type for FormularioAvaliacao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFormularioAvaliacao extends EntityPathBase<FormularioAvaliacao> {

    private static final long serialVersionUID = -954960620L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFormularioAvaliacao formularioAvaliacao = new QFormularioAvaliacao("formularioAvaliacao");

    public final BooleanPath ativo = createBoolean("ativo");

    public final ListPath<CicloAvaliacao, QCicloAvaliacao> ciclosAutoavaliacao = this.<CicloAvaliacao, QCicloAvaliacao>createList("ciclosAutoavaliacao", CicloAvaliacao.class, QCicloAvaliacao.class, PathInits.DIRECT2);

    public final ListPath<CicloAvaliacao, QCicloAvaliacao> ciclosAvaliacaoGestor = this.<CicloAvaliacao, QCicloAvaliacao>createList("ciclosAvaliacaoGestor", CicloAvaliacao.class, QCicloAvaliacao.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> dataAtualizacao = createDateTime("dataAtualizacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final StringPath descricao = createString("descricao");

    public final QFormularioAvaliacao formularioBase;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final ListPath<QuestaoAvaliacao, QQuestaoAvaliacao> questoes = this.<QuestaoAvaliacao, QQuestaoAvaliacao>createList("questoes", QuestaoAvaliacao.class, QQuestaoAvaliacao.class, PathInits.DIRECT2);

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoFormularioAvaliacao> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoFormularioAvaliacao.class);

    public final NumberPath<Integer> versao = createNumber("versao", Integer.class);

    public QFormularioAvaliacao(String variable) {
        this(FormularioAvaliacao.class, forVariable(variable), INITS);
    }

    public QFormularioAvaliacao(Path<? extends FormularioAvaliacao> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFormularioAvaliacao(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFormularioAvaliacao(PathMetadata metadata, PathInits inits) {
        this(FormularioAvaliacao.class, metadata, inits);
    }

    public QFormularioAvaliacao(Class<? extends FormularioAvaliacao> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.formularioBase = inits.isInitialized("formularioBase") ? new QFormularioAvaliacao(forProperty("formularioBase"), inits.get("formularioBase")) : null;
    }

}

