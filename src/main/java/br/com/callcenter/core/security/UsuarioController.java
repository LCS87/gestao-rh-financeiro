package br.com.callcenter.core.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import br.com.callcenter.core.annotation.Audited;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('TI')")
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    @Audited(acao = "UPDATE", tabela = "usuarios")
    public ResponseEntity<Usuario> atualizar(@PathVariable UUID id, @RequestBody Usuario dados) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setUsername(dados.getUsername());
            if (dados.getRole() != null) {
                usuario.setRole(dados.getRole());
            }
            if (dados.getPassword() != null && !dados.getPassword().trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(dados.getPassword()));
            }
            usuario.setEnabled(dados.isEnabled());
            return ResponseEntity.ok(usuarioRepository.save(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TI')")
    @Audited(acao = "DELETE", tabela = "usuarios")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
