package com.multitenant.ticker.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String tenantKey;
    private String displayName;
    public RegisterDto(String username, String password, String tenantKey, String displayName) {
        this.username = username;
        this.password = password;
        this.tenantKey = tenantKey;
        this.displayName = displayName;
    }
}
