package br.com.callcenter.modulorh.domain.enums;

public enum StatusAvaliacaoDesempenho {
    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída"),
    APROVADA("Aprovada"),
    REPROVADA("Reprovada"),
    AGUARDANDO_REVISAO("Aguardando revisão"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusAvaliacaoDesempenho(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtiva() {
        return this == PENDENTE || this == EM_ANDAMENTO || this == AGUARDANDO_REVISAO;
    }

    public boolean isFinalizada() {
        return this == CONCLUIDA || this == APROVADA || this == REPROVADA || this == CANCELADA;
    }

    public boolean isAprovada() {
        return this == APROVADA;
    }
}