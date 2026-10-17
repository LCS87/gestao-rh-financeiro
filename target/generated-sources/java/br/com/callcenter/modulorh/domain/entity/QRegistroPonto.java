package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRegistroPonto is a Querydsl query type for RegistroPonto
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRegistroPonto extends EntityPathBase<RegistroPonto> {

    private static final long serialVersionUID = -1905358684L;

    public static final QRegistroPonto registroPonto = new QRegistroPonto("registroPonto");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DatePath<java.time.LocalDate> data = createDate("data", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final TimePath<java.time.LocalTime> horaEntrada = createTime("horaEntrada", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> horaIntervaloFim = createTime("horaIntervaloFim", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> horaIntervaloInicio = createTime("horaIntervaloInicio", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> horaSaida = createTime("horaSaida", java.time.LocalTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath observacoes = createString("observacoes");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QRegistroPonto(String variable) {
        super(RegistroPonto.class, forVariable(variable));
    }

    public QRegistroPonto(Path<? extends RegistroPonto> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRegistroPonto(PathMetadata metadata) {
        super(RegistroPonto.class, metadata);
    }

}

