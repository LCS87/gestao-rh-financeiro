package br.com.callcenter.modulorh.domain.enums;

public enum TipoFormularioAvaliacao {
    AUTOAVALIACAO("Autoavaliação"),
    AVALIACAO_GESTOR("Avaliação do Gestor"),
    AVALIACAO_360("Avaliação 360°"),
    AVALIACAO_ESTAGIARIO("Avaliação de Estagiário"),
    AVALIACAO_PROBATORIO("Avaliação de Período Probatório");

    private final String descricao;

    TipoFormularioAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}