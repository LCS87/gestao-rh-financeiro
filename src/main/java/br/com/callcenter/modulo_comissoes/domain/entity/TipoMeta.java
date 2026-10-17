package br.com.callcenter.modulo_comissoes.domain.entity;

public enum TipoMeta {
    MENSAL("Mensal"),
    SEMANAL("Semanal"),
    DIARIA("Diária"),
    TRIMESTRAL("Trimestral");

    private final String descricao;

    TipoMeta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
