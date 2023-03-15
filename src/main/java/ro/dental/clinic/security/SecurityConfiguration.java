package ro.dental.clinic.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import ro.dental.clinic.config.KeycloakRealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()

                .antMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/refresh").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/logout").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/requests").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/employees").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/employees").permitAll()

                .antMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.PATCH, "/api/v1/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()

                .and()
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtConverter;
    }
}