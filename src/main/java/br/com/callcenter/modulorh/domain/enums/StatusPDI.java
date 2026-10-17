package br.com.callcenter.modulorh.domain.enums;

public enum StatusPDI {
    RASCUNHO("Rascunho"),
    APROVADO("Aprovado"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    SUSPENSO("Suspenso"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPDI(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return this == APROVADO || this == EM_ANDAMENTO;
    }

    public boolean isConcluido() {
        return this == CONCLUIDO;
    }
}