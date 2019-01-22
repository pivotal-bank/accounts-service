package io.pivotal.accounts.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class GrantedAuthoritiesConverter extends JwtAuthenticationConverter {

    @Override
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<String> scopes = (Collection<String>) jwt.getClaims().get("scope");
        return scopes.stream().filter(scope -> !scope.equals("openid"))
                .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope.toUpperCase().replaceAll("\\.", "_")))
                .collect(Collectors.toSet());
    }
}