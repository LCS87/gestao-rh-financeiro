package br.com.callcenter.moduloanalytics.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalyticsIndicator is a Querydsl query type for AnalyticsIndicator
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalyticsIndicator extends EntityPathBase<AnalyticsIndicator> {

    private static final long serialVersionUID = -1290575688L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalyticsIndicator analyticsIndicator = new QAnalyticsIndicator("analyticsIndicator");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final br.com.callcenter.modulorh.domain.entity.QDepartamento departamento;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath indicador = createString("indicador");

    public final StringPath periodo = createString("periodo");

    public final NumberPath<Double> valor = createNumber("valor", Double.class);

    public QAnalyticsIndicator(String variable) {
        this(AnalyticsIndicator.class, forVariable(variable), INITS);
    }

    public QAnalyticsIndicator(Path<? extends AnalyticsIndicator> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalyticsIndicator(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalyticsIndicator(PathMetadata metadata, PathInits inits) {
        this(AnalyticsIndicator.class, metadata, inits);
    }

    public QAnalyticsIndicator(Class<? extends AnalyticsIndicator> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.departamento = inits.isInitialized("departamento") ? new br.com.callcenter.modulorh.domain.entity.QDepartamento(forProperty("departamento")) : null;
    }

}

