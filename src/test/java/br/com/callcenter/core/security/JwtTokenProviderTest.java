package br.com.callcenter.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "chave-de-teste-com-no-minimo-32-caracteres-para-hs256";

    private JwtTokenProvider provider;
    private Usuario usuarioTi;
    private Usuario usuarioRh;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, 86400000L, 604800000L);
        usuarioTi = Usuario.builder().username("ti").role(Role.ROLE_TI).password("x").build();
        usuarioRh = Usuario.builder().username("rh").role(Role.ROLE_RH).password("x").build();
    }

    @Test
    @DisplayName("Gera token de acesso valido para o usuario")
    void deveGerarEValidarTokenAcesso() {
        String token = provider.generateAccessToken(usuarioTi);

        assertTrue(provider.isTokenValid(token, usuarioTi));
        assertEquals("ti", provider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Token nao e valido para outro usuario (sujeito diferente)")
    void deveRejeitarTokenDeOutroUsuario() {
        String token = provider.generateAccessToken(usuarioTi);

        assertFalse(provider.isTokenValid(token, usuarioRh));
    }

    @Test
    @DisplayName("Token invalido nao passa na validacao")
    void deveRejeitarTokenInvalido() {
        assertThrows(Exception.class, () -> provider.isTokenValid("um.token.qualquer", usuarioTi));
    }

    @Test
    @DisplayName("Token expirado deve ser rejeitado")
    void deveRejeitarTokenExpirado() {
        JwtTokenProvider expiring = new JwtTokenProvider(SECRET, -1000L, 604800000L);
        String token = expiring.generateAccessToken(usuarioTi);

        // parser lanca ExpiredJwtException ao ler claims de token expirado
        assertThrows(Exception.class, () -> expiring.isTokenValid(token, usuarioTi));
    }

    @Test
    @DisplayName("Gera refresh token com o sujeito correto")
    void deveGerarRefreshToken() {
        String token = provider.generateRefreshToken(usuarioRh);

        assertEquals("rh", provider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Secret curto deve falhar na inicializacao")
    void deveRejeitarSecretCurto() {
        assertThrows(IllegalArgumentException.class,
                () -> new JwtTokenProvider("curto", 86400000L, 604800000L));
    }
}
