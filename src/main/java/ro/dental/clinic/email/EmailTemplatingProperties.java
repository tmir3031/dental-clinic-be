package ro.dental.clinic.email;


import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import ro.dental.clinic.readmodels.YamlPropertySourceFactory;

/**
 * Configuration properties containing the email templating details
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "email-templating")
@PropertySource(value = "classpath:email-templates.yml", factory = YamlPropertySourceFactory.class)
public class EmailTemplatingProperties {
    private Map<String, EmailTemplate> templates;

}
