package br.com.callcenter.core.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secret",
                "chave-de-teste-com-no-minimo-32-caracteres-para-hs256");
        ReflectionTestUtils.setField(provider, "expiration", 86400000L);
        ReflectionTestUtils.setField(provider, "refreshExpiration", 604800000L);
    }

    @Test
    @DisplayName("Gera token de acesso valido para o usuario")
    void deveGerarEValidarTokenAcesso() {
        String token = provider.generateToken("ti", "ROLE_TI");

        assertTrue(provider.validateToken(token));
        assertEquals("ti", provider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Token invalido nao passa na validacao")
    void deveRejeitarTokenInvalido() {
        assertFalse(provider.validateToken("um.token.qualquer"));
    }

    @Test
    @DisplayName("Claims contem a autoridade (role) do usuario")
    void deveConterRoleNasClaims() {
        String token = provider.generateToken("rh", "ROLE_RH");
        Claims claims = provider.getClaimsFromToken(token);
        assertNotNull(claims.get("authorities"));
    }
}
