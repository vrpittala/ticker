package com.multitenant.ticker.initializer;

import com.multitenant.ticker.entity.Role;
import com.multitenant.ticker.entity.UserEntity;
import com.multitenant.ticker.repo.RoleRepository;
import com.multitenant.ticker.repo.UserRepository;
import com.multitenant.ticker.security.JwtProperties;
import com.multitenant.ticker.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(RoleInitializer.class);

    public RoleInitializer(RoleRepository roleRepository, UserRepository userRepository, JwtProperties jwtProperties, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String ... args) throws Exception {
        // Initialize roles if they do not exist
        createRolesIfNotExist("ADMIN");
        createRolesIfNotExist("TENANT_ADMIN");
        createRolesIfNotExist("USER");

        createSuperAdmin();
    }

    private void createRolesIfNotExist(String roleName) {
        if(!this.roleRepository.existsByName(roleName)){
            Role role = new Role(roleName);
            this.roleRepository.save(role);
            log.info("Created role: " + roleName);
        }
    }

    private void createSuperAdmin() {
        UserEntity superAdminUser = new UserEntity();
        superAdminUser.setUsername(this.jwtProperties.getAdminUsername());
        superAdminUser.setPassword(this.passwordEncoder.encode(this.jwtProperties.getAdminPassword()));
        Role superAdminRole = this.roleRepository.findByName("ADMIN").orElse(null);
        if (superAdminRole != null) {
            superAdminUser.addRole(superAdminRole);
        }
        Role tenantAdminRole = this.roleRepository.findByName("TENANT_ADMIN").orElse(null);
        if (tenantAdminRole != null) {
            superAdminUser.addRole(tenantAdminRole);
        }
        Role userRole = this.roleRepository.findByName("USER").orElse(null);
        if (userRole != null) {
            superAdminUser.addRole(userRole);
        }
        this.userRepository.save(superAdminUser);
        log.info("Created super admin user: " + superAdminUser.getUsername());
    }

}
