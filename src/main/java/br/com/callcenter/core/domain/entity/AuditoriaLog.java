package br.com.callcenter.core.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "usuario_nome")
    private String usuarioNome;

    // CREATE, UPDATE, DELETE, etc
    @Column(nullable = false, length = 50)
    private String acao;

    @Column(nullable = false, length = 100)
    private String tabela;

    @Column(name = "registro_id")
    private String registroId;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
