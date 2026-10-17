package br.com.callcenter.core.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QParametroSistema is a Querydsl query type for ParametroSistema
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParametroSistema extends EntityPathBase<ParametroSistema> {

    private static final long serialVersionUID = 1491133609L;

    public static final QParametroSistema parametroSistema = new QParametroSistema("parametroSistema");

    public final StringPath chave = createString("chave");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath descricao = createString("descricao");

    public final StringPath tipo = createString("tipo");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath valor = createString("valor");

    public QParametroSistema(String variable) {
        super(ParametroSistema.class, forVariable(variable));
    }

    public QParametroSistema(Path<? extends ParametroSistema> path) {
        super(path.getType(), path.getMetadata());
    }

    public QParametroSistema(PathMetadata metadata) {
        super(ParametroSistema.class, metadata);
    }

}

