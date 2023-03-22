package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ro.dental.clinic.config.RolesProperties;
import ro.dental.clinic.model.RolesDetailsList;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(RolesProperties.class)
public class RoleService {

    private final RolesProperties rolesProperties;

    public RolesDetailsList getRoles() {
        var rolesList = new RolesDetailsList();
        rolesList.setRoles(rolesProperties.getRoles());
        return rolesList;
    }
}