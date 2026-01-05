package com.multitenant.ticker.controllers;

import com.multitenant.ticker.dto.UpgradePlanRequestDto;
import com.multitenant.ticker.services.TenantAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;


@RestController
@RequestMapping("api/tenants")
@PreAuthorize("hasRole('TENANT_ADMIN')")
public class TenantAdminController {

    private final TenantAdminService tenantAdminService;

    public TenantAdminController(TenantAdminService tenantAdminService) {
        this.tenantAdminService = tenantAdminService;
    }

    @PostMapping("upgrade")
    public ResponseEntity<String> upgradeToPremium(@RequestBody UpgradePlanRequestDto upgradePlanRequestDto) {
        // Logic to upgrade tenant to premium
        return this.tenantAdminService.upgradeTenant(upgradePlanRequestDto.getPlanType());
    }
}
