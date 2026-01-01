package com.multitenant.ticker.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private String tenantKey;
    public LoginDto(String username, String password, String tenantKey) {
        this.username = username;
        this.password = password;
        this.tenantKey = tenantKey;
    }
}
