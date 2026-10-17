package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTurno is a Querydsl query type for Turno
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTurno extends EntityPathBase<Turno> {

    private static final long serialVersionUID = -1796275041L;

    public static final QTurno turno = new QTurno("turno");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final NumberPath<Integer> diaSemana = createNumber("diaSemana", Integer.class);

    public final ComparablePath<java.util.UUID> escalaId = createComparable("escalaId", java.util.UUID.class);

    public final TimePath<java.time.LocalTime> horaEntrada = createTime("horaEntrada", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> horaSaida = createTime("horaSaida", java.time.LocalTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final TimePath<java.time.LocalTime> intervaloFim = createTime("intervaloFim", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> intervaloInicio = createTime("intervaloInicio", java.time.LocalTime.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QTurno(String variable) {
        super(Turno.class, forVariable(variable));
    }

    public QTurno(Path<? extends Turno> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTurno(PathMetadata metadata) {
        super(Turno.class, metadata);
    }

}

