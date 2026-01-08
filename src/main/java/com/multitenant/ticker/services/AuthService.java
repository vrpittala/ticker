package com.multitenant.ticker.services;

import com.multitenant.ticker.context.TenantContext;
import com.multitenant.ticker.dto.AuthResponseDto;
import com.multitenant.ticker.dto.LoginDto;
import com.multitenant.ticker.dto.RegisterDto;
import com.multitenant.ticker.entity.Role;
import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.entity.UserEntity;
import com.multitenant.ticker.enums.PlanType;
import com.multitenant.ticker.enums.TenantStatus;
import com.multitenant.ticker.repo.RoleRepository;
import com.multitenant.ticker.repo.TenantRepository;
import com.multitenant.ticker.repo.UserRepository;
import com.multitenant.ticker.security.JwtGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final TenantService tenantService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(TenantService tenantService, UserService userService, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, RoleRepository roleRepository, AuthenticationManager authenticationManager) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
    }

    public void assertSuperAdmin(UserEntity user) {
        Role adminRole = this.roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        if (!user.getRoles().contains(adminRole)) {
            log.warn("User {} is not a super admin", user.getUsername());
            throw new RuntimeException("User is not a super admin");
        }
    }

    @Transactional
    public ResponseEntity<AuthResponseDto> register(RegisterDto registerDto) {
        log.info("Starting registration for user: {}", registerDto.getUsername());
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
        Role roles = this.roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        newUser.setRoles(List.of(roles));
        this.userService.saveUser(newUser);
        TenantContext.clear();
        log.info("Saved user {} to DB", registerDto.getUsername());
        String jwtToken = this.jwtGenerator.generateToken(newUser, tenant);
        log.debug("JWT token: {}", jwtToken);
        return new ResponseEntity<>(new AuthResponseDto("user registered successfully", jwtToken), HttpStatus.OK);
    }

    public ResponseEntity<AuthResponseDto> login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        String tenantKey = loginDto.getTenantKey();
        if(tenantKey == null){
            UserEntity user = this.userService.resolveUserByUsername(username);
            if(user!=null) {
                this.assertSuperAdmin(user);
                log.info("Starting login for super admin user: {}", username);
                UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(username, password);
                Authentication authentication = this.authenticationManager.authenticate(upaToken);
                authentication.getAuthorities().forEach(authority -> log.info("Granted Authority: {}", authority.getAuthority()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwtToken = this.jwtGenerator.generateSuperAdminToken(user);
                log.debug("JWT token: {}", jwtToken);
                return new ResponseEntity<>(new AuthResponseDto("User logged in successfully", jwtToken), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AuthResponseDto("User not found"), HttpStatus.BAD_REQUEST);

        }else {
            log.info("Starting login for user: {}", username);
            if(this.tenantService.tenantExistsByTenantKey(tenantKey)){
                Tenant tenant = this.tenantService.resolveTenantByKey(tenantKey);
                UserEntity user = this.userService.resolveUserByUsernameAndTenantId(username, tenant.getId());
                if(user == null){
                    log.warn("User {} not found for tenant {}", username, tenantKey);
                    return new ResponseEntity<>(new AuthResponseDto("User not found"), HttpStatus.BAD_REQUEST);
                }
                TenantContext.setTenantId(tenant.getId());
                Authentication authentication;
                try {
                    UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(username, password);
                    authentication = this.authenticationManager.authenticate(upaToken);
                    authentication.getAuthorities().forEach(authority -> log.info("Granted Authority: {}", authority.getAuthority()));
                }
                finally {
                    TenantContext.clear();
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwtToken = this.jwtGenerator.generateToken(user, tenant);
                log.debug("JWT token: {}", jwtToken);
                return new ResponseEntity<>(new AuthResponseDto("User logged in successfully", jwtToken), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new AuthResponseDto("User not found"), HttpStatus.BAD_REQUEST);

    }
}
