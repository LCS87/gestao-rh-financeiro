package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.EquipeDto;
import br.com.callcenter.modulorh.application.EquipeService;
import br.com.callcenter.modulorh.domain.entity.Equipe;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/equipes")
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Equipe>> listar() {
        return ResponseEntity.ok(equipeService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Equipe> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(equipeService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Equipe> criar(@Valid @RequestBody EquipeDto dto) {
        Equipe criada = equipeService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Equipe> atualizar(@PathVariable UUID id,
                                            @Valid @RequestBody EquipeDto dto) {
        return ResponseEntity.ok(equipeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        equipeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

