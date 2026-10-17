package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "escalas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Escala {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEscala tipo;

    private String descricao;

    @Column(name = "horas_semanais", nullable = false)
    private Double horasSemanais = 44.0;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    public enum TipoEscala {
        COMERCIAL,      // Segunda a Sexta, 8h
        C6X1,           // 6 dias trabalhados, 1 folga
        C12X36,         // 12h trabalho, 36h folga
        PLANTAO,        // Plantões variados
        OUTRO
    }
}
