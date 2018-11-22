package io.pivotal.accounts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private GrantedAuthoritiesConverter grantedAuthoritiesConverter;

    @Autowired
    private OAuth2ResourceServerProperties resourceServerProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .anonymous().disable()
                .authorizeRequests().anyRequest().hasAuthority("ROLE_ACCOUNT")
                .and().oauth2ResourceServer()
                .jwt().jwtAuthenticationConverter(grantedAuthoritiesConverter).decoder(jwtDecoder());
    }

    private JwtDecoder jwtDecoder() {
        String issuerUri = this.resourceServerProperties.getJwt().getIssuerUri();

        NimbusJwtDecoderJwkSupport jwtDecoder =
                (NimbusJwtDecoderJwkSupport) JwtDecoders.fromOidcIssuerLocation(issuerUri);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        jwtDecoder.setJwtValidator(withIssuer);

        return jwtDecoder;
    }
}
