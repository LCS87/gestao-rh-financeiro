package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.TipoReacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "curtidas_postagem", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"postagem_id", "funcionario_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurtidaPostagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postagem_id", nullable = false)
    private PostagemMural postagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoReacao tipoReacao = TipoReacao.CURTIR;

    @Column(name = "data_curtida", nullable = false)
    private LocalDateTime dataCurtida = LocalDateTime.now();

    // Construtor auxiliar
    public CurtidaPostagem(PostagemMural postagem, Funcionario funcionario, TipoReacao tipoReacao) {
        this.postagem = postagem;
        this.funcionario = funcionario;
        this.tipoReacao = tipoReacao;
        this.dataCurtida = LocalDateTime.now();
    }
}