package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ferias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ferias {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "funcionario_id", nullable = false)
    private UUID funcionarioId;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private Integer dias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFerias tipo = TipoFerias.INTEGRAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFerias status = StatusFerias.AGENDADA;

    private String observacoes;

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

    public enum TipoFerias {
        INTEGRAL, FRACIONADA
    }

    public enum StatusFerias {
        AGENDADA, APROVADA, EM_GOZO, CONCLUIDA, CANCELADA
    }
}
