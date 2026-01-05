package com.multitenant.ticker.services;

import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.enums.PlanType;
import com.multitenant.ticker.enums.TenantStatus;
import com.multitenant.ticker.repo.TenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.multitenant.ticker.context.TenantContext;

import java.util.UUID;

@Service
public class TenantAdminService {

    private final TenantRepository tenantRepository;
    private final TenantService tenantService;

    public TenantAdminService(TenantRepository tenantRepository, TenantService tenantService) {
        this.tenantRepository = tenantRepository;
        this.tenantService = tenantService;
    }

    @Transactional
    public ResponseEntity<String> upgradeTenant(PlanType planType) {
        // Logic to upgrade tenant to premium
        UUID tenantId = TenantContext.getTenantId();
        if(tenantId == null){
            throw new RuntimeException("No tenant context available");
        }
        if(!this.tenantService.tenantExistsById(tenantId)){
            throw new RuntimeException("Tenant not found");
        }

        Tenant tenant = this.tenantService.resolveTenantById(tenantId);
        if(tenant.getStatus() != TenantStatus.ACTIVE){
            throw new RuntimeException("Only active tenants can be upgraded to premium");
        }

        if(tenant.getPlan() == planType) {
            throw new RuntimeException("Tenant is already on the requested plan");
        }
        tenant.setPlan(planType);
        this.tenantService.saveTenant(tenant);
        return ResponseEntity.ok("Tenant upgraded to " + planType + " successfully.");

    }
}
