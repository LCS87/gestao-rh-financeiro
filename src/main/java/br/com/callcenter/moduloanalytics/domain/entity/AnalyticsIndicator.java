package br.com.callcenter.moduloanalytics.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analytics_indicators")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Nome do indicador: rotatividade, absenteismo, custo_por_funcionario, etc.
    @Column(nullable = false, length = 50)
    private String indicador;

    // Valor calculado do indicador
    @Column(nullable = false, precision = 19, scale = 4)
    private Double valor;

    // Período: YYYY-MM
    @Column(nullable = false, length = 7)
    private String periodo;

    // Departamento (opcional - se null, é geral da empresa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private br.com.callcenter.modulorh.domain.entity.Departamento departamento;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
