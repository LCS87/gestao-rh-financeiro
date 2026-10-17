package br.com.callcenter.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotacao para marcar metodos que devem ter suas acoes logadas para a TI.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    String acao(); // Ex: INSERT, UPDATE, DELETE
    String tabela(); // Ex: Funcionario, Venda
}
