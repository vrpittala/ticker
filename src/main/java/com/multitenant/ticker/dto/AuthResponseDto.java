package com.multitenant.ticker.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private String message;
    public AuthResponseDto(String message, String token) {
        this.message = message;
        this.accessToken = token;
    }

    public AuthResponseDto(String message) {
        this.message = message;
    }
}
