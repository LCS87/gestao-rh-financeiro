package br.com.callcenter.modulorh.domain.enums;

public enum TipoPostagem {
    PUBLICACAO("Publicação"),
    ANUNCIO("Anúncio"),
    NOTICIA("Notícia"),
    EVENTO("Evento"),
    DICA("Dica"),
    RECONHECIMENTO("Reconhecimento"),
    RESULTADO("Resultado"),
    OUTRO("Outro");

    private final String descricao;

    TipoPostagem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}