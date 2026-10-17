package br.com.callcenter.core.api;

import br.com.callcenter.core.application.ParametroSistemaService;
import br.com.callcenter.core.dto.ParametroSistemaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametros")
@RequiredArgsConstructor
@Tag(name = "Parâmetros do Sistema", description = "Endpoints para gerenciamento de Configurações Globais")
public class ParametroSistemaController {

    private final ParametroSistemaService parametroService;

    @GetMapping
    @Operation(summary = "Lista todos os parâmetros (Admin/TI)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TI')")
    public ResponseEntity<List<ParametroSistemaDto>> listarTodos() {
        return ResponseEntity.ok(parametroService.listarTodos());
    }

    @GetMapping("/{chave}")
    @Operation(summary = "Busca parâmetro por chave")
    public ResponseEntity<String> getParametro(@PathVariable String chave,
                                              @RequestParam(defaultValue = "") String padrao) {
        return ResponseEntity.ok(parametroService.getValorParametro(chave, padrao));
    }

    @PostMapping
    @Operation(summary = "Adiciona ou Atualiza um parâmetro (Admin/TI)")
    @PreAuthorize("hasAnyRole('ADMIN', 'TI')")
    public ResponseEntity<ParametroSistemaDto> salvarParametro(@RequestBody ParametroSistemaDto dto) {
        return ResponseEntity.ok(parametroService.salvarParametro(dto));
    }
    
    @PostMapping("/limpar-cache")
    @Operation(summary = "Limpa o cache do sistema inteiro de parâmetros")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> limparCache() {
        parametroService.limparCache();
        return ResponseEntity.noContent().build();
    }
}
