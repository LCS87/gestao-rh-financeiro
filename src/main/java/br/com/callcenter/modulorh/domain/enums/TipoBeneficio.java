package br.com.callcenter.modulorh.domain.enums;

public enum TipoBeneficio {
    VALE_TRANSPORTE("Vale Transporte"),
    VALE_REFEICAO("Vale Refeição"),
    VALE_ALIMENTACAO("Vale Alimentação"),
    PLANO_SAUDE("Plano de Saúde"),
    PLANO_ODONTOLOGICO("Plano Odontológico"),
    SEGURO_VIDA("Seguro de Vida"),
    AUXILIO_CRECHE("Auxílio Creche"),
    AUXILIO_EDUCACAO("Auxílio Educação"),
    PARTICIPACAO_NOS_LUCROS("Participação nos Lucros"),
    PREVIDENCIA_PRIVADA("Previdência Privada"),
    GYM_PASS("Gym Pass"),
    OUTROS("Outros");

    private final String descricao;

    TipoBeneficio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}