package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdmissao is a Querydsl query type for Admissao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdmissao extends EntityPathBase<Admissao> {

    private static final long serialVersionUID = 1358912480L;

    public static final QAdmissao admissao = new QAdmissao("admissao");

    public final BooleanPath certificadoReservista = createBoolean("certificadoReservista");

    public final BooleanPath comprovanteResidenciaEntregue = createBoolean("comprovanteResidenciaEntregue");

    public final BooleanPath contratoAssinado = createBoolean("contratoAssinado");

    public final BooleanPath cpfEntregue = createBoolean("cpfEntregue");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final BooleanPath ctpsEntregue = createBoolean("ctpsEntregue");

    public final DatePath<java.time.LocalDate> dataAdmissao = createDate("dataAdmissao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataAssinaturaContrato = createDate("dataAssinaturaContrato", java.time.LocalDate.class);

    public final BooleanPath exameMedicoRealizado = createBoolean("exameMedicoRealizado");

    public final DatePath<java.time.LocalDate> exameMedicoValidade = createDate("exameMedicoValidade", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath observacoes = createString("observacoes");

    public final ComparablePath<java.util.UUID> responsavelAdmissaoId = createComparable("responsavelAdmissaoId", java.util.UUID.class);

    public final BooleanPath rgEntregue = createBoolean("rgEntregue");

    public final EnumPath<Admissao.StatusAdmissao> status = createEnum("status", Admissao.StatusAdmissao.class);

    public final BooleanPath tituloEleitorEntregue = createBoolean("tituloEleitorEntregue");

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QAdmissao(String variable) {
        super(Admissao.class, forVariable(variable));
    }

    public QAdmissao(Path<? extends Admissao> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdmissao(PathMetadata metadata) {
        super(Admissao.class, metadata);
    }

}

