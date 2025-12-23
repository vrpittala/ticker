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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final RoleRepository roleRepository;

    public AuthService(TenantRepository tenantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, RoleRepository roleRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<String> register(RegisterDto registerDto) {
        if(this.userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        String tenantKey = registerDto.getTenantKey();
        Tenant tenant = this.tenantRepository.findByTenantKey(tenantKey)
                .orElse(null);
        UUID tenantId = tenant!=null ? tenant.getId() : null;
        if(!this.tenantRepository.existsByTenantKey(tenantKey)){
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
        Role roles = this.roleRepository.findByRoleName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRoles(List.of(roles));
        this.userRepository.save(newUser);
        String jwtToken = this.jwtGenerator.generateToken(newUser, tenant);
        return new ResponseEntity<>("User Registered Successfully", HttpStatus.OK);
    }
}
