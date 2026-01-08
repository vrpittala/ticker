package com.multitenant.ticker.services;

import com.multitenant.ticker.context.TenantContext;
import com.multitenant.ticker.dto.AuthResponseDto;
import com.multitenant.ticker.dto.RegisterDto;
import com.multitenant.ticker.entity.Role;
import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.entity.UserEntity;
import com.multitenant.ticker.enums.PlanType;
import com.multitenant.ticker.enums.TenantStatus;
import com.multitenant.ticker.repo.RoleRepository;
import com.multitenant.ticker.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final TenantService tenantService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    public AdminService(TenantService tenantService, UserService userService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    public final ResponseEntity<AuthResponseDto> addTenantAdmin(RegisterDto registerDto) {
        // Logic to add a new tenant admin
        log.info("Starting registration for tenant admin: {}", registerDto.getUsername());
        String tenantKey = registerDto.getTenantKey();
        Tenant tenant = this.tenantService.resolveTenantByKey(registerDto.getTenantKey());
        if(tenant!=null && this.userService.userExistsByUsernameAndTenantId(registerDto.getUsername(), tenant.getId())) {
            log.warn("Username {} already exists", registerDto.getUsername());
            return new ResponseEntity<AuthResponseDto>(new AuthResponseDto("Username already exists"), HttpStatus.BAD_REQUEST);
        }
        UUID tenantId = tenant!=null ? tenant.getId() : null;
        if(!this.tenantService.tenantExistsByTenantKey(tenantKey)){
            log.info("Adding new tenant: {}", registerDto.getDisplayName());
            Tenant newTenant = new Tenant();
            newTenant.setCreated(Instant.now());
            newTenant.setLastUpdated(Instant.now());
            tenant = newTenant;
            newTenant.setTenantKey(registerDto.getTenantKey());
            newTenant.setDisplayName(registerDto.getDisplayName());
            newTenant.setPlan(PlanType.FREE);
            newTenant.setStatus(TenantStatus.ACTIVE);
            this.tenantService.saveTenant(newTenant);
            tenantId = newTenant.getId();
        }
        TenantContext.setTenantId(tenantId);
        UserEntity newUser = new UserEntity();
        newUser.setUsername(registerDto.getUsername());
        newUser.setTenantId(tenantId);
        newUser.setPassword(this.passwordEncoder.encode(registerDto.getPassword()));
        Role tenantAdminRole = this.roleRepository.findByName("TENANT_ADMIN").orElse(null);
        if (tenantAdminRole != null) {
            newUser.addRole(tenantAdminRole);
        }
        Role userRole = this.roleRepository.findByName("USER").orElse(null);
        if (userRole != null) {
            newUser.addRole(userRole);
        }
        this.userService.saveUser(newUser);
        TenantContext.clear();
        log.info("Saved Tenant Admin {} to DB", registerDto.getUsername());
        return new ResponseEntity<>(new AuthResponseDto("Tenant Admin registered successfully"), HttpStatus.OK);
    }
}
