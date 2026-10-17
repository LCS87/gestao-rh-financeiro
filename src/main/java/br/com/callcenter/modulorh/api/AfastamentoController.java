package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.AfastamentoDto;
import br.com.callcenter.modulorh.application.AfastamentoService;
import br.com.callcenter.modulorh.domain.entity.Afastamento;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/afastamentos")
public class AfastamentoController {

    private final AfastamentoService afastamentoService;

    public AfastamentoController(AfastamentoService afastamentoService) {
        this.afastamentoService = afastamentoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Afastamento>> listar() {
        return ResponseEntity.ok(afastamentoService.listarTodos());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Afastamento>> listarPorFuncionario(@PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(afastamentoService.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Afastamento> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(afastamentoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Afastamento> criar(@Valid @RequestBody AfastamentoDto dto) {
        Afastamento criado = afastamentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Afastamento> atualizar(@PathVariable UUID id, @Valid @RequestBody AfastamentoDto dto) {
        return ResponseEntity.ok(afastamentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        afastamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
