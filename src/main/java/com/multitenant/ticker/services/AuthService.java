package com.multitenant.ticker.services;

import com.multitenant.ticker.dto.AuthResponseDto;
import com.multitenant.ticker.dto.RegisterDto;
import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.repo.TenantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final TenantRepository tenantRepository;

    public AuthService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public ResponseEntity<String> register(RegisterDto registerDto) {
        String tenantKey = registerDto.getTenantKey();
        if(!tenantRepository.existsByTenantKey(tenantKey)){
            Tenant newTenant = new Tenant();
            newTenant.setTenantKey(registerDto.getTenantKey());
            newTenant.setDisplayName(registerDto.getDisplayName());
            tenantRepository.save(newTenant);
            return new ResponseEntity<>("User Registered Successfully", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("User Registered Successfully", HttpStatus.OK);
        }
    }
}
