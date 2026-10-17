package br.com.callcenter.moduloanalytics.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "analytics_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Nome da métrica: admissoes, demissoes, faltas, ferias, etc.
    @Column(nullable = false, length = 50)
    private String metrica;

    // Valor da métrica
    @Column(nullable = false)
    private Integer valor;

    // Data da métrica
    @Column(nullable = false)
    private LocalDate data;

    // Departamento (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private br.com.callcenter.modulorh.domain.entity.Departamento departamento;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}
