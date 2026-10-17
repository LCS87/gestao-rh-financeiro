package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOpcaoEnquete is a Querydsl query type for OpcaoEnquete
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOpcaoEnquete extends EntityPathBase<OpcaoEnquete> {

    private static final long serialVersionUID = -147160116L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOpcaoEnquete opcaoEnquete = new QOpcaoEnquete("opcaoEnquete");

    public final QEnqueteMural enquete;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> ordem = createNumber("ordem", Integer.class);

    public final StringPath texto = createString("texto");

    public QOpcaoEnquete(String variable) {
        this(OpcaoEnquete.class, forVariable(variable), INITS);
    }

    public QOpcaoEnquete(Path<? extends OpcaoEnquete> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOpcaoEnquete(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOpcaoEnquete(PathMetadata metadata, PathInits inits) {
        this(OpcaoEnquete.class, metadata, inits);
    }

    public QOpcaoEnquete(Class<? extends OpcaoEnquete> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.enquete = inits.isInitialized("enquete") ? new QEnqueteMural(forProperty("enquete"), inits.get("enquete")) : null;
    }

}

