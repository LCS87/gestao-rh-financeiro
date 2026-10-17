package br.com.callcenter.modulorh.domain.enums;

public enum StatusFeedbackAvaliacao {
    PENDENTE("Pendente"),
    AGENDADO("Agendado"),
    AGUARDANDO_GESTOR("Aguardando feedback do gestor"),
    AGUARDANDO_FUNCIONARIO("Aguardando feedback do funcionário"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusFeedbackAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return this == AGENDADO || this == AGUARDANDO_GESTOR || this == AGUARDANDO_FUNCIONARIO;
    }

    public boolean isConcluido() {
        return this == CONCLUIDO;
    }
}