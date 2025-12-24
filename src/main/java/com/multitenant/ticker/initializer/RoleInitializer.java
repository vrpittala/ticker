package com.multitenant.ticker.initializer;

import com.multitenant.ticker.entity.Role;
import com.multitenant.ticker.repo.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String ... args) throws Exception {
        // Initialize roles if they do not exist
        createRolesIfNotExist("ADMIN");
        createRolesIfNotExist("TENANT_ADMIN");
        createRolesIfNotExist("USER");
    }

    private void createRolesIfNotExist(String roleName) {
        if(!this.roleRepository.existsByName(roleName)){
            Role role = new Role(roleName);
            this.roleRepository.save(role);
        }
    }

}
