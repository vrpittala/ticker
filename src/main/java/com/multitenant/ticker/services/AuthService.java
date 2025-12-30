package com.multitenant.ticker.services;

import com.multitenant.ticker.dto.AuthResponseDto;
import com.multitenant.ticker.dto.RegisterDto;
import com.multitenant.ticker.entity.Role;
import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.entity.UserEntity;
import com.multitenant.ticker.enums.TenantStatus;
import com.multitenant.ticker.repo.RoleRepository;
import com.multitenant.ticker.repo.TenantRepository;
import com.multitenant.ticker.repo.UserRepository;
import com.multitenant.ticker.security.JwtGenerator;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(TenantRepository tenantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, RoleRepository roleRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public ResponseEntity<String> register(RegisterDto registerDto) {
        log.info("Starting registration for user: {}", registerDto.getUsername());
        if(this.userRepository.existsByUsername(registerDto.getUsername())){
            log.warn("Username {} already exists", registerDto.getUsername());
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        String tenantKey = registerDto.getTenantKey();
        Tenant tenant = this.tenantRepository.findByTenantKey(tenantKey)
                .orElse(null);
        UUID tenantId = tenant!=null ? tenant.getId() : null;
        if(!this.tenantRepository.existsByTenantKey(tenantKey)){
            log.info("Adding new tenant: {}", registerDto.getDisplayName());
            Tenant newTenant = new Tenant();
            tenant = newTenant;
            newTenant.setTenantKey(registerDto.getTenantKey());
            newTenant.setDisplayName(registerDto.getDisplayName());
            newTenant.setStatus(TenantStatus.ACTIVE);
            this.tenantRepository.save(newTenant);
            tenantId = newTenant.getId();
        }
        UserEntity newUser = new UserEntity();
        newUser.setUsername(registerDto.getUsername());
        newUser.setTenantId(tenantId);
        newUser.setPassword(this.passwordEncoder.encode(registerDto.getPassword()));
        Role roles = this.roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRoles(List.of(roles));
        this.userRepository.save(newUser);
        log.info("Saved user {} to DB", registerDto.getUsername());
        String jwtToken = this.jwtGenerator.generateToken(newUser, tenant);
        log.debug("JWT token: {}", jwtToken);
        return new ResponseEntity<>("user registered successfully", HttpStatus.OK);
    }
}
