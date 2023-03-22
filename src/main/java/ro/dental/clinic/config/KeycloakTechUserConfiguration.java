package ro.dental.clinic.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({KeycloakProperties.class})
public class KeycloakTechUserConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public KeycloakTechUserAccessTokenProvider techUserAccessTokenProvider(Clock clock,
                                                                           KeycloakProperties keycloakProperties) {
        return new KeycloakTechUserAccessTokenProvider(clock, keycloakProperties);
    }
}
