package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.AdmissaoDto;
import br.com.callcenter.modulorh.application.AdmissaoService;
import br.com.callcenter.modulorh.domain.entity.Admissao;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/admissoes")
public class AdmissaoController {

    private final AdmissaoService admissaoService;

    public AdmissaoController(AdmissaoService admissaoService) {
        this.admissaoService = admissaoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<List<Admissao>> listar() {
        return ResponseEntity.ok(admissaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Admissao> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(admissaoService.buscarPorId(id));
    }

    @GetMapping("/funcionario/{funcionarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Admissao> buscarPorFuncionario(@PathVariable UUID funcionarioId) {
        return ResponseEntity.ok(admissaoService.buscarPorFuncionarioId(funcionarioId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Admissao> criar(@Valid @RequestBody AdmissaoDto dto) {
        Admissao criada = admissaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Admissao> atualizar(@PathVariable UUID id,
                                              @Valid @RequestBody AdmissaoDto dto) {
        return ResponseEntity.ok(admissaoService.atualizar(id, dto));
    }

    @PostMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Admissao> concluir(@PathVariable UUID id) {
        return ResponseEntity.ok(admissaoService.concluirAdmissao(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        admissaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
