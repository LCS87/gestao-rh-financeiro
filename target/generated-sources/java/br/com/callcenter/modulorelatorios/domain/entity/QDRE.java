package br.com.callcenter.modulorelatorios.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDRE is a Querydsl query type for DRE
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDRE extends EntityPathBase<DRE> {

    private static final long serialVersionUID = -2031616020L;

    public static final QDRE dRE = new QDRE("dRE");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Double> custoVendas = createNumber("custoVendas", Double.class);

    public final NumberPath<Double> despesasAdministrativas = createNumber("despesasAdministrativas", Double.class);

    public final NumberPath<Double> despesasComerciais = createNumber("despesasComerciais", Double.class);

    public final NumberPath<Double> despesasFinanceiras = createNumber("despesasFinanceiras", Double.class);

    public final NumberPath<Double> despesasTotal = createNumber("despesasTotal", Double.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Double> impostos = createNumber("impostos", Double.class);

    public final NumberPath<Double> lucroAntesImposto = createNumber("lucroAntesImposto", Double.class);

    public final NumberPath<Double> lucroBruto = createNumber("lucroBruto", Double.class);

    public final NumberPath<Double> lucroLiquido = createNumber("lucroLiquido", Double.class);

    public final NumberPath<Double> lucroOperacional = createNumber("lucroOperacional", Double.class);

    public final NumberPath<Double> outrasDespesas = createNumber("outrasDespesas", Double.class);

    public final NumberPath<Double> outrasReceitas = createNumber("outrasReceitas", Double.class);

    public final StringPath periodo = createString("periodo");

    public final NumberPath<Double> receitaTotal = createNumber("receitaTotal", Double.class);

    public final NumberPath<Double> receitaVendas = createNumber("receitaVendas", Double.class);

    public final NumberPath<Double> resultadoFinanceiro = createNumber("resultadoFinanceiro", Double.class);

    public QDRE(String variable) {
        super(DRE.class, forVariable(variable));
    }

    public QDRE(Path<? extends DRE> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDRE(PathMetadata metadata) {
        super(DRE.class, metadata);
    }

}

