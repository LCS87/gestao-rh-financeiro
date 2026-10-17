package br.com.callcenter.core.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAuditoriaLog is a Querydsl query type for AuditoriaLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuditoriaLog extends EntityPathBase<AuditoriaLog> {

    private static final long serialVersionUID = -1416441650L;

    public static final QAuditoriaLog auditoriaLog = new QAuditoriaLog("auditoriaLog");

    public final StringPath acao = createString("acao");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath detalhes = createString("detalhes");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final StringPath registroId = createString("registroId");

    public final StringPath tabela = createString("tabela");

    public final ComparablePath<java.util.UUID> usuarioId = createComparable("usuarioId", java.util.UUID.class);

    public final StringPath usuarioNome = createString("usuarioNome");

    public QAuditoriaLog(String variable) {
        super(AuditoriaLog.class, forVariable(variable));
    }

    public QAuditoriaLog(Path<? extends AuditoriaLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuditoriaLog(PathMetadata metadata) {
        super(AuditoriaLog.class, metadata);
    }

}

