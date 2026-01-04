package com.multitenant.ticker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;


@RestController
@RequestMapping("api/tenants")
@PreAuthorize("hasRole('TENANT_ADMIN')")
public class TenantAdminController {

    @PostMapping("upgrade-to-premium")
    public ResponseEntity<String> upgradeToPremium() {
        // Logic to upgrade tenant to premium
        return ResponseEntity.ok("Tenant upgraded to premium successfully.");
    }
}
