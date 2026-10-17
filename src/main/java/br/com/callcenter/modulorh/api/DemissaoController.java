package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.DemissaoDto;
import br.com.callcenter.modulorh.application.DemissaoService;
import br.com.callcenter.modulorh.domain.entity.Demissao;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/demissoes")
public class DemissaoController {

    private final DemissaoService demissaoService;

    public DemissaoController(DemissaoService demissaoService) {
        this.demissaoService = demissaoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH','FINANCEIRO')")
    public ResponseEntity<List<Demissao>> listar() {
        return ResponseEntity.ok(demissaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','FINANCEIRO')")
    public ResponseEntity<Demissao> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(demissaoService.buscarPorId(id));
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','FINANCEIRO')")
    public ResponseEntity<Demissao> buscarPorFuncionario(@PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(demissaoService.buscarPorFuncionarioId(funcionarioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Demissao> criar(@Valid @RequestBody DemissaoDto dto) {
        Demissao criada = demissaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Demissao> atualizar(@PathVariable UUID id,
                                             @Valid @RequestBody DemissaoDto dto) {
        return ResponseEntity.ok(demissaoService.atualizar(id, dto));
    }

    @PostMapping("/{id}/calcular-rescisao")
    @PreAuthorize("hasAnyRole('ADMIN','RH','FINANCEIRO')")
    public ResponseEntity<Demissao> calcularRescisao(@PathVariable UUID id) {
        return ResponseEntity.ok(demissaoService.calcularRescisao(id));
    }

    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Demissao> finalizar(@PathVariable UUID id) {
        return ResponseEntity.ok(demissaoService.finalizarDemissao(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        demissaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
