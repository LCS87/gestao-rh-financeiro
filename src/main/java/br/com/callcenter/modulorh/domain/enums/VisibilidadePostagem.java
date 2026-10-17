package br.com.callcenter.modulorh.domain.enums;

public enum VisibilidadePostagem {
    PUBLICO("Público - Todos os funcionários"),
    DEPARTAMENTO("Apenas departamento"),
    EQUIPE("Apenas equipe"),
    PRIVADO("Privado - Apenas selecionados");

    private final String descricao;

    VisibilidadePostagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}