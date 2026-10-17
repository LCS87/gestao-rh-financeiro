package br.com.callcenter.modulo_comissoes.domain.entity;

public enum TipoComissao {
    PORCENTAGEM_VENDA("Porcentagem sobre Venda"),
    VALOR_FIXO("Valor Fixo"),
    FAIXA_VENDA("Faixa de Venda"),
    BONUS_META("Bônus por Meta");

    private final String descricao;

    TipoComissao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
