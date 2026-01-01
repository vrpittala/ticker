package com.multitenant.ticker.services;

import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.repo.TenantRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant resolveTenantByKey(String tenantKey) {
        return this.tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    public Boolean tenantExistsByTenantKey(String tenantKey) {
        return this.tenantRepository.existsByTenantKey(tenantKey);
    }

    public void saveTenant(Tenant tenant) {
        this.tenantRepository.save(tenant);
    }
}
