package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.DepartamentoDto;
import br.com.callcenter.modulorh.application.DepartamentoService;
import br.com.callcenter.modulorh.domain.entity.Departamento;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<List<Departamento>> listar() {
        return ResponseEntity.ok(departamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Departamento> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(departamentoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Departamento> criar(@Valid @RequestBody DepartamentoDto dto) {
        Departamento criado = departamentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Departamento> atualizar(@PathVariable UUID id,
                                                  @Valid @RequestBody DepartamentoDto dto) {
        return ResponseEntity.ok(departamentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        departamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

