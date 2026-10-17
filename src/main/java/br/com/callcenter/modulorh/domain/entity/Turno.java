package br.com.callcenter.modulorh.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "turnos", uniqueConstraints = @UniqueConstraint(columnNames = {"escala_id", "dia_semana"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "escala_id", nullable = false)
    private UUID escalaId;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 1=domingo, 2=segunda, ..., 7=sábado

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @Column(name = "hora_saida", nullable = false)
    private LocalTime horaSaida;

    @Column(name = "intervalo_inicio")
    private LocalTime intervaloInicio;

    @Column(name = "intervalo_fim")
    private LocalTime intervaloFim;

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
}
