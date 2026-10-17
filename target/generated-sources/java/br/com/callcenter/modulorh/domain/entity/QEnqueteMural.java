package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEnqueteMural is a Querydsl query type for EnqueteMural
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEnqueteMural extends EntityPathBase<EnqueteMural> {

    private static final long serialVersionUID = 134211423L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEnqueteMural enqueteMural = new QEnqueteMural("enqueteMural");

    public final BooleanPath anonima = createBoolean("anonima");

    public final BooleanPath ativa = createBoolean("ativa");

    public final QFuncionario autor;

    public final DateTimePath<java.time.LocalDateTime> dataCriacao = createDateTime("dataCriacao", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> dataExpiracao = createDateTime("dataExpiracao", java.time.LocalDateTime.class);

    public final StringPath descricao = createString("descricao");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ListPath<OpcaoEnquete, QOpcaoEnquete> opcoes = this.<OpcaoEnquete, QOpcaoEnquete>createList("opcoes", OpcaoEnquete.class, QOpcaoEnquete.class, PathInits.DIRECT2);

    public final StringPath pergunta = createString("pergunta");

    public final BooleanPath permiteMultiplaEscolha = createBoolean("permiteMultiplaEscolha");

    public final ListPath<VotoEnquete, QVotoEnquete> votos = this.<VotoEnquete, QVotoEnquete>createList("votos", VotoEnquete.class, QVotoEnquete.class, PathInits.DIRECT2);

    public QEnqueteMural(String variable) {
        this(EnqueteMural.class, forVariable(variable), INITS);
    }

    public QEnqueteMural(Path<? extends EnqueteMural> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEnqueteMural(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEnqueteMural(PathMetadata metadata, PathInits inits) {
        this(EnqueteMural.class, metadata, inits);
    }

    public QEnqueteMural(Class<? extends EnqueteMural> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.autor = inits.isInitialized("autor") ? new QFuncionario(forProperty("autor")) : null;
    }

}

