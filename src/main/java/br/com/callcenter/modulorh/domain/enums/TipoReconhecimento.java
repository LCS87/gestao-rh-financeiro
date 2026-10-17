package br.com.callcenter.modulorh.domain.enums;

public enum TipoReconhecimento {
    PARABENS("🎉 Parabéns"),
    OBRIGADO("🙏 Obrigado"),
    EXCELENTE_TRABALHO("🌟 Excelente Trabalho"),
    COLABORACAO("🤝 Colaboração"),
    INOVACAO("💡 Inovação"),
    LIDERANCA("👑 Liderança"),
    MELHORIA_CONTINUA("📈 Melhoria Contínua"),
    ESPIRITO_EQUIPE("🏆 Espírito de Equipe");

    private final String descricao;

    TipoReconhecimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}