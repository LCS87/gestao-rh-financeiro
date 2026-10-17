package br.com.callcenter.modulorh.api;

import br.com.callcenter.modulorh.api.dto.FuncionarioDto;
import br.com.callcenter.modulorh.application.FuncionarioService;
import br.com.callcenter.modulorh.domain.entity.Funcionario;
import br.com.callcenter.core.annotation.Audited;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rh/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<List<Funcionario>> listar() {
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH','SUPERVISOR')")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Funcionario> criar(@Valid @RequestBody FuncionarioDto dto) {
        Funcionario criado = funcionarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<Funcionario> atualizar(@PathVariable UUID id,
                                                 @Valid @RequestBody FuncionarioDto dto) {
        return ResponseEntity.ok(funcionarioService.atualizar(id, dto));
    }

    @Operation(summary = "Excluir funcionário (Apenas TI)")
    @PreAuthorize("hasRole('TI')")
    @DeleteMapping("/{id}")
    @Audited(acao = "DELETE", tabela = "funcionarios")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
