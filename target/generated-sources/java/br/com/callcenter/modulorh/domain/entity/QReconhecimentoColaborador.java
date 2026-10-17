package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReconhecimentoColaborador is a Querydsl query type for ReconhecimentoColaborador
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReconhecimentoColaborador extends EntityPathBase<ReconhecimentoColaborador> {

    private static final long serialVersionUID = 1965158552L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReconhecimentoColaborador reconhecimentoColaborador = new QReconhecimentoColaborador("reconhecimentoColaborador");

    public final BooleanPath ativo = createBoolean("ativo");

    public final DateTimePath<java.time.LocalDateTime> dataReconhecimento = createDateTime("dataReconhecimento", java.time.LocalDateTime.class);

    public final QFuncionario destinatario;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath mensagem = createString("mensagem");

    public final StringPath motivo = createString("motivo");

    public final BooleanPath publico = createBoolean("publico");

    public final QFuncionario remetente;

    public final EnumPath<br.com.callcenter.modulorh.domain.enums.TipoReconhecimento> tipo = createEnum("tipo", br.com.callcenter.modulorh.domain.enums.TipoReconhecimento.class);

    public QReconhecimentoColaborador(String variable) {
        this(ReconhecimentoColaborador.class, forVariable(variable), INITS);
    }

    public QReconhecimentoColaborador(Path<? extends ReconhecimentoColaborador> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReconhecimentoColaborador(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReconhecimentoColaborador(PathMetadata metadata, PathInits inits) {
        this(ReconhecimentoColaborador.class, metadata, inits);
    }

    public QReconhecimentoColaborador(Class<? extends ReconhecimentoColaborador> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.destinatario = inits.isInitialized("destinatario") ? new QFuncionario(forProperty("destinatario")) : null;
        this.remetente = inits.isInitialized("remetente") ? new QFuncionario(forProperty("remetente")) : null;
    }

}

