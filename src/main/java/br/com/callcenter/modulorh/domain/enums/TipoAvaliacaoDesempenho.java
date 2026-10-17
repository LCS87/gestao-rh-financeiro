package br.com.callcenter.modulorh.domain.enums;

public enum TipoAvaliacaoDesempenho {
    AUTOAVALIACAO("Autoavaliação"),
    AVALIACAO_GESTOR("Avaliação do Gestor"),
    AVALIACAO_360("Avaliação 360°"),
    AVALIACAO_PARES("Avaliação de Pares"),
    AVALIACAO_SUBORDINADOS("Avaliação de Subordinados"),
    AVALIACAO_CLIENTES("Avaliação de Clientes");

    private final String descricao;

    TipoAvaliacaoDesempenho(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAutoavaliacao() {
        return this == AUTOAVALIACAO;
    }

    public boolean isAvaliacaoGestor() {
        return this == AVALIACAO_GESTOR;
    }
}