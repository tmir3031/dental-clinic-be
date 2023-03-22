package ro.dental.clinic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "ro.dental.clinic")

public class RolesProperties {

    private List<String> roles;
}
