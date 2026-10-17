package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVotoEnquete is a Querydsl query type for VotoEnquete
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVotoEnquete extends EntityPathBase<VotoEnquete> {

    private static final long serialVersionUID = 1853099938L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVotoEnquete votoEnquete = new QVotoEnquete("votoEnquete");

    public final DateTimePath<java.time.LocalDateTime> dataVoto = createDateTime("dataVoto", java.time.LocalDateTime.class);

    public final QEnqueteMural enquete;

    public final QFuncionario funcionario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final QOpcaoEnquete opcao;

    public QVotoEnquete(String variable) {
        this(VotoEnquete.class, forVariable(variable), INITS);
    }

    public QVotoEnquete(Path<? extends VotoEnquete> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVotoEnquete(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVotoEnquete(PathMetadata metadata, PathInits inits) {
        this(VotoEnquete.class, metadata, inits);
    }

    public QVotoEnquete(Class<? extends VotoEnquete> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.enquete = inits.isInitialized("enquete") ? new QEnqueteMural(forProperty("enquete"), inits.get("enquete")) : null;
        this.funcionario = inits.isInitialized("funcionario") ? new QFuncionario(forProperty("funcionario")) : null;
        this.opcao = inits.isInitialized("opcao") ? new QOpcaoEnquete(forProperty("opcao"), inits.get("opcao")) : null;
    }

}

