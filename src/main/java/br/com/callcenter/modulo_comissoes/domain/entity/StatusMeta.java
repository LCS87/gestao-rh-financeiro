package br.com.callcenter.modulo_comissoes.domain.entity;

public enum StatusMeta {
    ATIVA("Ativa"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    EM_ANDAMENTO("Em Andamento");

    private final String descricao;

    StatusMeta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
