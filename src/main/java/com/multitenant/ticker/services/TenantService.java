package com.multitenant.ticker.services;

import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.repo.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant resolveTenantByKey(String tenantKey) {
        return this.tenantRepository.findByTenantKey(tenantKey)
                .orElse(null);
    }

    public Tenant resolveTenantById(UUID tenantId) {
        return this.tenantRepository.findById(tenantId)
                .orElse(null);
    }

    public Boolean tenantExistsByTenantKey(String tenantKey) {
        return this.tenantRepository.existsByTenantKey(tenantKey);
    }

    public Boolean tenantExistsById(UUID tenantId) {
        return this.tenantRepository.existsById(tenantId);
    }

    public void saveTenant(Tenant tenant) {
        this.tenantRepository.save(tenant);
    }
}
