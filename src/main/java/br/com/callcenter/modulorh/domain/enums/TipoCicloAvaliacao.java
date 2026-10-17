package br.com.callcenter.modulorh.domain.enums;

public enum TipoCicloAvaliacao {
    TRIMESTRAL("Trimestral"),
    SEMESTRAL("Semestral"),
    ANUAL("Anual"),
    ESPECIAL("Especial");

    private final String descricao;

    TipoCicloAvaliacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPeriodosPorAno() {
        return switch (this) {
            case TRIMESTRAL -> 4;
            case SEMESTRAL -> 2;
            case ANUAL -> 1;
            default -> 1;
        };
    }
}