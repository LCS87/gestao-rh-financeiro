package br.com.callcenter.core.security;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static br.com.callcenter.core.security.AuthDto.LoginRequest;
import static br.com.callcenter.core.security.AuthDto.RefreshRequest;
import static br.com.callcenter.core.security.AuthDto.TokenResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "gestao-rh-financeiro"));
    }

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(usuario);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, "Bearer"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para o refresh token"));

        String accessToken = jwtTokenProvider.generateAccessToken(usuario);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, "Bearer"));
    }
}

