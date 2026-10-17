package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "enquetes_mural")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnqueteMural {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Funcionario autor;

    @Column(nullable = false, length = 500)
    private String pergunta;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @Column(nullable = false)
    private Boolean anonima = false;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "permite_multipla_escolha", nullable = false)
    private Boolean permiteMultiplaEscolha = false;

    @OneToMany(mappedBy = "enquete", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<OpcaoEnquete> opcoes = new ArrayList<>();

    @OneToMany(mappedBy = "enquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotoEnquete> votos = new ArrayList<>();

    // Construtores auxiliares
    public EnqueteMural(Funcionario autor, String pergunta, Boolean anonima) {
        this.autor = autor;
        this.pergunta = pergunta;
        this.anonima = anonima;
        this.ativa = true;
        this.dataCriacao = LocalDateTime.now();
    }

    // Métodos auxiliares
    public void adicionarOpcao(String textoOpcao, Integer ordem) {
        OpcaoEnquete opcao = new OpcaoEnquete(this, textoOpcao, ordem);
        this.opcoes.add(opcao);
    }

    public Long getTotalVotos() {
        return (long) this.votos.size();
    }
}