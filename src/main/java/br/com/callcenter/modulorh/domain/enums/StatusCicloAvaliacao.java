package br.com.callcenter.modulorh.domain.enums;

public enum StatusCicloAvaliacao {
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    EM_AUTOAVALIACAO("Em autoavaliação"),
    EM_AVALIACAO_GESTOR("Em avaliação do gestor"),
    EM_FEEDBACK("Em feedback"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusCicloAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return this == EM_ANDAMENTO || 
               this == EM_AUTOAVALIACAO || 
               this == EM_AVALIACAO_GESTOR || 
               this == EM_FEEDBACK;
    }

    public boolean isConcluido() {
        return this == CONCLUIDO;
    }

    public boolean isCancelado() {
        return this == CANCELADO;
    }
}