package com.multitenant.ticker.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    public AuthResponseDto(String token) {
        this.accessToken = token;
    }
}
