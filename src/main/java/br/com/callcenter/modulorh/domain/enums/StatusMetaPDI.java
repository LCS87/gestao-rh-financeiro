package br.com.callcenter.modulorh.domain.enums;

public enum StatusMetaPDI {
    PLANEJADA("Planejada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    ATRASADA("Atrasada"),
    SUSPENSA("Suspensa"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusMetaPDI(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtiva() {
        return this == PLANEJADA || this == EM_ANDAMENTO;
    }

    public boolean isConcluida() {
        return this == CONCLUIDA;
    }
}