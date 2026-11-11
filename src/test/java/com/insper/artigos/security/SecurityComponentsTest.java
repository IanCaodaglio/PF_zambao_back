package com.insper.artigos.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class SecurityComponentsTest {

    private static final String CLAIM = "https://avaliacoes-api/roles";

    private Jwt jwt(Map<String, Object> claims) {
        Jwt.Builder builder = Jwt.withTokenValue("t")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60));
        claims.forEach(builder::claim);
        return builder.build();
    }

    @Test
    void audienceValidatorAceitaAudienceCorreto() {
        var result = new AudienceValidator("https://avaliacoes-api")
                .validate(jwt(Map.of("aud", List.of("https://avaliacoes-api", "outro"))));
        assertFalse(result.hasErrors());
    }

    @Test
    void audienceValidatorRejeitaAudienceErrado() {
        var result = new AudienceValidator("https://avaliacoes-api")
                .validate(jwt(Map.of("aud", List.of("https://outra-api"))));
        assertTrue(result.hasErrors());
    }

    @Test
    void audienceValidatorRejeitaTokenSemAudience() {
        var result = new AudienceValidator("https://avaliacoes-api")
                .validate(jwt(Map.of("sub", "alguem")));
        assertTrue(result.hasErrors());
    }

    @Test
    void rolesConverterPrefixaComRole() {
        var authorities = new RolesConverter(CLAIM)
                .convert(jwt(Map.of(CLAIM, List.of("ADMIN", "USER"))));
        assertEquals(List.of("ROLE_ADMIN", "ROLE_USER"),
                authorities.stream().map(GrantedAuthority::getAuthority).toList());
    }

    @Test
    void rolesConverterSemClaimNaoDaPermissao() {
        var authorities = new RolesConverter(CLAIM).convert(jwt(Map.of("sub", "alguem")));
        assertTrue(authorities.isEmpty());
    }
}
