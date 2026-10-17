package br.com.callcenter.modulofinanceiro.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFolhaPagamento is a Querydsl query type for FolhaPagamento
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFolhaPagamento extends EntityPathBase<FolhaPagamento> {

    private static final long serialVersionUID = 2008233159L;

    public static final QFolhaPagamento folhaPagamento = new QFolhaPagamento("folhaPagamento");

    public final NumberPath<java.math.BigDecimal> adicionalNoturno = createNumber("adicionalNoturno", java.math.BigDecimal.class);

    public final StringPath agenciaPagamento = createString("agenciaPagamento");

    public final StringPath anoMes = createString("anoMes");

    public final StringPath bancoPagamento = createString("bancoPagamento");

    public final NumberPath<java.math.BigDecimal> bonus = createNumber("bonus", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> comissoes = createNumber("comissoes", java.math.BigDecimal.class);

    public final StringPath contaPagamento = createString("contaPagamento");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataPagamento = createDate("dataPagamento", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataProcessamento = createDate("dataProcessamento", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> decimoTerceiro = createNumber("decimoTerceiro", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> ferias = createNumber("ferias", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> fgts = createNumber("fgts", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> funcionarioId = createComparable("funcionarioId", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> horasExtras = createNumber("horasExtras", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> inss = createNumber("inss", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> irrf = createNumber("irrf", java.math.BigDecimal.class);

    public final StringPath observacoes = createString("observacoes");

    public final NumberPath<java.math.BigDecimal> outrosDescontos = createNumber("outrosDescontos", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> outrosProventos = createNumber("outrosProventos", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> planoSaude = createNumber("planoSaude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> salarioBase = createNumber("salarioBase", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> salarioLiquido = createNumber("salarioLiquido", java.math.BigDecimal.class);

    public final EnumPath<FolhaPagamento.StatusFolha> status = createEnum("status", FolhaPagamento.StatusFolha.class);

    public final NumberPath<java.math.BigDecimal> totalDescontos = createNumber("totalDescontos", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> totalProventos = createNumber("totalProventos", java.math.BigDecimal.class);

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public final NumberPath<java.math.BigDecimal> valeRefeicao = createNumber("valeRefeicao", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> valeTransporte = createNumber("valeTransporte", java.math.BigDecimal.class);

    public QFolhaPagamento(String variable) {
        super(FolhaPagamento.class, forVariable(variable));
    }

    public QFolhaPagamento(Path<? extends FolhaPagamento> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFolhaPagamento(PathMetadata metadata) {
        super(FolhaPagamento.class, metadata);
    }

}

