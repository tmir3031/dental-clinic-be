package ro.dental.clinic.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ro.dental.clinic.config.KeycloakProperties;

@Configuration
public class KeycloakApiUserFactory {

    @Bean
    public KeycloakUserApi keycloakUserApi(KeycloakProperties keycloakProperties,
                                           RestTemplate restTemplate) {
        return new KeycloakUserApi(keycloakProperties.getApi(), restTemplate);
    }

}
