package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDemissao is a Querydsl query type for Demissao
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDemissao extends EntityPathBase<Demissao> {

    private static final long serialVersionUID = -1115087426L;

    public static final QDemissao demissao = new QDemissao("demissao");

    public final BooleanPath avisoPrevioIndenizado = createBoolean("avisoPrevioIndenizado");

    public final NumberPath<java.math.BigDecimal> avisoPrevioIndenizadoValor = createNumber("avisoPrevioIndenizadoValor", java.math.BigDecimal.class);

    public final BooleanPath avisoPrevioTrabalhado = createBoolean("avisoPrevioTrabalhado");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataCalculo = createDate("dataCalculo", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataDemissao = createDate("dataDemissao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataExameDemissional = createDate("dataExameDemissional", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> decimoTerceiro = createNumber("decimoTerceiro", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> decimoTerceiroProporcional = createNumber("decimoTerceiroProporcional", java.math.BigDecimal.class);

    public final BooleanPath exameDemissionalRealizado = createBoolean("exameDemissionalRealizado");

    public final NumberPath<java.math.BigDecimal> feriasProporcionais = createNumber("feriasProporcionais", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> feriasVencidas = createNumber("feriasVencidas", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> fgts = createNumber("fgts", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath motivo = createString("motivo");

    public final NumberPath<java.math.BigDecimal> multaFgts = createNumber("multaFgts", java.math.BigDecimal.class);

    public final StringPath observacoes = createString("observacoes");

    public final ComparablePath<java.util.UUID> responsavelDemissaoId = createComparable("responsavelDemissaoId", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> saldoSalario = createNumber("saldoSalario", java.math.BigDecimal.class);

    public final EnumPath<Demissao.StatusDemissao> status = createEnum("status", Demissao.StatusDemissao.class);

    public final EnumPath<Demissao.TipoDemissao> tipoDemissao = createEnum("tipoDemissao", Demissao.TipoDemissao.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> valorTotalRescisao = createNumber("valorTotalRescisao", java.math.BigDecimal.class);

    public QDemissao(String variable) {
        super(Demissao.class, forVariable(variable));
    }

    public QDemissao(Path<? extends Demissao> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDemissao(PathMetadata metadata) {
        super(Demissao.class, metadata);
    }

}

