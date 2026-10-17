package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "opcoes_enquete")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpcaoEnquete {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private EnqueteMural enquete;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(nullable = false)
    private Integer ordem;

    // Construtor auxiliar
    public OpcaoEnquete(EnqueteMural enquete, String texto, Integer ordem) {
        this.enquete = enquete;
        this.texto = texto;
        this.ordem = ordem;
    }
}