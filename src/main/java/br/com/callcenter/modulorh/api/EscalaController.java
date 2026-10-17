package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.EscalaDto;
import br.com.callcenter.modulorh.application.EscalaService;
import br.com.callcenter.modulorh.domain.entity.Escala;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/escalas")
public class EscalaController {

    private final EscalaService escalaService;

    public EscalaController(EscalaService escalaService) {
        this.escalaService = escalaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Escala>> listar() {
        return ResponseEntity.ok(escalaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Escala> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(escalaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Escala> criar(@Valid @RequestBody EscalaDto dto) {
        Escala criada = escalaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Escala> atualizar(@PathVariable UUID id, @Valid @RequestBody EscalaDto dto) {
        return ResponseEntity.ok(escalaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        escalaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
