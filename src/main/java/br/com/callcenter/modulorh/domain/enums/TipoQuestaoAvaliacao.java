package br.com.callcenter.modulorh.domain.enums;

public enum TipoQuestaoAvaliacao {
    ESCALA_LIKERT("Escala Likert (1-5)"),
    ESCALA_NUMERICA("Escala Numérica (0-10)"),
    MULTIPLA_ESCOLHA("Múltipla Escolha"),
    UNICA_ESCOLHA("Única Escolha"),
    TEXTO_LIVRE("Texto Livre (curto)"),
    TEXTO_LONGO("Texto Longo (longo)"),
    SIM_NAO("Sim/Não");

    private final String descricao;

    TipoQuestaoAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isQuestaoNumerica() {
        return this == ESCALA_LIKERT || this == ESCALA_NUMERICA;
    }

    public boolean isQuestaoTexto() {
        return this == TEXTO_LIVRE || this == TEXTO_LONGO;
    }

    public boolean isQuestaoEscolha() {
        return this == MULTIPLA_ESCOLHA || this == UNICA_ESCOLHA || this == SIM_NAO;
    }

    public boolean isSimNao() {
        return this == SIM_NAO;
    }
}