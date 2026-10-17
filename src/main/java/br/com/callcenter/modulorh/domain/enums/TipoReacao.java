package br.com.callcenter.modulorh.domain.enums;

public enum TipoReacao {
    CURTIR("👍 Curtir"),
    AMAR("❤️ Amar"),
    RIR("😂 Rir"),
    SURPREENDER("😮 Surpreender"),
    TRISTE("😢 Triste"),
    RAIVA("😠 Raiva");

    private final String descricao;

    TipoReacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}