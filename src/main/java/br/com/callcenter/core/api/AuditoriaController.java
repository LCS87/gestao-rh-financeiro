package br.com.callcenter.core.api;

import br.com.callcenter.core.domain.entity.AuditoriaLog;
import br.com.callcenter.core.dto.AuditoriaLogDto;
import br.com.callcenter.core.repository.AuditoriaLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@Tag(name = "Auditoria TI", description = "Monitoramento de Log e Ações Destrutivas (Fase 5)")
public class AuditoriaController {

    private final AuditoriaLogRepository logRepository;

    @GetMapping("/recentes")
    @Operation(summary = "Puxa as últimas 50 atividades cruciais (CRUD) do sistema")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<List<AuditoriaLogDto>> listarRecentes() {
        List<AuditoriaLogDto> list = logRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private AuditoriaLogDto mapToDto(AuditoriaLog log) {
        return AuditoriaLogDto.builder()
                .id(log.getId())
                .usuarioId(log.getUsuarioId())
                .usuarioNome(log.getUsuarioNome())
                .acao(log.getAcao())
                .tabela(log.getTabela())
                .registroId(log.getRegistroId())
                .detalhes(log.getDetalhes())
                .ipAddress(log.getIpAddress())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
