package br.com.callcenter.modulorh.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFuncionario is a Querydsl query type for Funcionario
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFuncionario extends EntityPathBase<Funcionario> {

    private static final long serialVersionUID = -207956696L;

    public static final QFuncionario funcionario = new QFuncionario("funcionario");

    public final ComparablePath<java.util.UUID> cargoId = createComparable("cargoId", java.util.UUID.class);

    public final StringPath cpf = createString("cpf");

    public final DatePath<java.time.LocalDate> createdAt = createDate("createdAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataAdmissao = createDate("dataAdmissao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataDemissao = createDate("dataDemissao", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> dataNascimento = createDate("dataNascimento", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> departamentoId = createComparable("departamentoId", java.util.UUID.class);

    public final StringPath email = createString("email");

    public final ComparablePath<java.util.UUID> equipeId = createComparable("equipeId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> escalaId = createComparable("escalaId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath nome = createString("nome");

    public final NumberPath<Double> salarioBase = createNumber("salarioBase", Double.class);

    public final EnumPath<Funcionario.StatusFuncionario> status = createEnum("status", Funcionario.StatusFuncionario.class);

    public final StringPath telefone = createString("telefone");

    public final DatePath<java.time.LocalDate> updatedAt = createDate("updatedAt", java.time.LocalDate.class);

    public QFuncionario(String variable) {
        super(Funcionario.class, forVariable(variable));
    }

    public QFuncionario(Path<? extends Funcionario> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFuncionario(PathMetadata metadata) {
        super(Funcionario.class, metadata);
    }

}

