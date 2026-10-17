package br.com.callcenter.modulorh.domain.entity;

import br.com.callcenter.modulorh.domain.enums.TipoReconhecimento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reconhecimentos_colaborador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconhecimentoColaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Funcionario remetente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Funcionario destinatario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoReconhecimento tipo = TipoReconhecimento.PARABENS;

    @Column(nullable = false, length = 1000)
    private String mensagem;

    @Column(length = 500)
    private String motivo;

    @Column(name = "data_reconhecimento", nullable = false)
    private LocalDateTime dataReconhecimento = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean publico = true;

    @Column(nullable = false)
    private Boolean ativo = true;

    // Construtor auxiliar
    public ReconhecimentoColaborador(Funcionario remetente, Funcionario destinatario, 
                                    TipoReconhecimento tipo, String mensagem) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.publico = true;
        this.ativo = true;
        this.dataReconhecimento = LocalDateTime.now();
    }
}