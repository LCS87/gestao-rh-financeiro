package br.com.callcenter.moduloanalytics.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnalyticsMetric is a Querydsl query type for AnalyticsMetric
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnalyticsMetric extends EntityPathBase<AnalyticsMetric> {

    private static final long serialVersionUID = 334003111L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnalyticsMetric analyticsMetric = new QAnalyticsMetric("analyticsMetric");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> data = createDate("data", java.time.LocalDate.class);

    public final br.com.callcenter.modulorh.domain.entity.QDepartamento departamento;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath metrica = createString("metrica");

    public final NumberPath<Integer> valor = createNumber("valor", Integer.class);

    public QAnalyticsMetric(String variable) {
        this(AnalyticsMetric.class, forVariable(variable), INITS);
    }

    public QAnalyticsMetric(Path<? extends AnalyticsMetric> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnalyticsMetric(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnalyticsMetric(PathMetadata metadata, PathInits inits) {
        this(AnalyticsMetric.class, metadata, inits);
    }

    public QAnalyticsMetric(Class<? extends AnalyticsMetric> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.departamento = inits.isInitialized("departamento") ? new br.com.callcenter.modulorh.domain.entity.QDepartamento(forProperty("departamento")) : null;
    }

}

