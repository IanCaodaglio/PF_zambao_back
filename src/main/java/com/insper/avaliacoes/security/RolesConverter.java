package com.insper.avaliacoes.security;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Converte a claim de papéis (adicionada pela Action do Auth0) em authorities ROLE_*.
 */
public class RolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final String rolesClaim;

    public RolesConverter(String rolesClaim) {
        this.rolesClaim = rolesClaim;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList(rolesClaim);
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
