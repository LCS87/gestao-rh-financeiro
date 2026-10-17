package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.FeriasDto;
import br.com.callcenter.modulorh.application.FeriasService;
import br.com.callcenter.modulorh.domain.entity.Ferias;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/ferias")
public class FeriasController {

    private final FeriasService feriasService;

    public FeriasController(FeriasService feriasService) {
        this.feriasService = feriasService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Ferias>> listar() {
        return ResponseEntity.ok(feriasService.listarTodas());
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Ferias>> listarPorFuncionario(@PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(feriasService.listarPorFuncionario(funcionarioId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Ferias> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(feriasService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Ferias> criar(@Valid @RequestBody FeriasDto dto) {
        Ferias criada = feriasService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Ferias> atualizar(@PathVariable UUID id, @Valid @RequestBody FeriasDto dto) {
        return ResponseEntity.ok(feriasService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        feriasService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
