package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votos_enquete", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"enquete_id", "funcionario_id", "opcao_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotoEnquete {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private EnqueteMural enquete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcao_id", nullable = false)
    private OpcaoEnquete opcao;

    @Column(name = "data_voto", nullable = false)
    private LocalDateTime dataVoto = LocalDateTime.now();

    // Construtor auxiliar
    public VotoEnquete(EnqueteMural enquete, Funcionario funcionario, OpcaoEnquete opcao) {
        this.enquete = enquete;
        this.funcionario = funcionario;
        this.opcao = opcao;
        this.dataVoto = LocalDateTime.now();
    }
}