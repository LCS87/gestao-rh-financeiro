package br.com.callcenter.core.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametros_sistema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametroSistema {

    @Id
    @Column(length = 100, nullable = false)
    private String chave;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String valor;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 50, nullable = false)
    @Builder.Default
    private String tipo = "STRING"; // STRING, NUMBER, BOOLEAN, JSON

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
