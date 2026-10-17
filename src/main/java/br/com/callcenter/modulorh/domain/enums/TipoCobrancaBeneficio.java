package br.com.callcenter.modulorh.domain.enums;

public enum TipoCobrancaBeneficio {
    VALOR_FIXO("Valor Fixo"),
    PERCENTUAL_SALARIO("Percentual sobre Salário"),
    VALOR_VARIAVEL("Valor Variável"),
    EMPRESA_CUSTEIA_TOTAL("Empresa custeia total"),
    FUNCIONARIO_CUSTEIA_TOTAL("Funcionário custeia total"),
    COMPARTILHADO("Compartilhado");

    private final String descricao;

    TipoCobrancaBeneficio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}