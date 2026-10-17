package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDocumento is a Querydsl query type for Documento
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDocumento extends EntityPathBase<Documento> {

    private static final long serialVersionUID = 92764481L;

    public static final QDocumento documento = new QDocumento("documento");

    public final StringPath caminhoArquivo = createString("caminhoArquivo");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataEmissao = createDate("dataEmissao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataUpload = createDate("dataUpload", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataValidade = createDate("dataValidade", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nomeArquivo = createString("nomeArquivo");

    public final StringPath observacoes = createString("observacoes");

    public final EnumPath<Documento.StatusDocumento> status = createEnum("status", Documento.StatusDocumento.class);

    public final NumberPath<Long> tamanhoArquivo = createNumber("tamanhoArquivo", Long.class);

    public final EnumPath<Documento.TipoDocumento> tipoDocumento = createEnum("tipoDocumento", Documento.TipoDocumento.class);

    public final StringPath tipoMime = createString("tipoMime");

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> uploadedById = createComparable("uploadedById", java.util.UUID.class);

    public QDocumento(String variable) {
        super(Documento.class, forVariable(variable));
    }

    public QDocumento(Path<? extends Documento> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDocumento(PathMetadata metadata) {
        super(Documento.class, metadata);
    }

}

