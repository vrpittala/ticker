package com.multitenant.ticker.controllers;

import com.multitenant.ticker.dto.AuthResponseDto;
import com.multitenant.ticker.dto.RegisterDto;
import com.multitenant.ticker.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("add-tenant-admin")
    public ResponseEntity<AuthResponseDto> addTenantAdmin(@RequestBody RegisterDto registerDto) {
        return this.adminService.addTenantAdmin(registerDto);
    }
}
