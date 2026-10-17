package br.com.callcenter.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaLogDto {
    private UUID id;
    private UUID usuarioId;
    private String usuarioNome;
    private String acao;
    private String tabela;
    private String registroId;
    private String detalhes;
    private String ipAddress;
    private LocalDateTime createdAt;
}
