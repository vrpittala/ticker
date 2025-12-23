package com.multitenant.ticker.security;

import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.entity.UserEntity;
import com.multitenant.ticker.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtGenerator {

    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtGenerator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken2(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + this.jwtProperties.getExpiration());
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
        return token;
    }

    public String generateToken(UserEntity user, Tenant tenant){
        String username = user.getUsername();
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenant.getId());
        claims.put("roles", user.getRoles().stream().map(Role::getName).toList());
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + this.jwtProperties.getExpiration());
        return Jwts.builder()
            .setSubject(username)
            .addClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(this.key, SignatureAlgorithm.HS512)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(this.jwtProperties.getSecret().getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT token is expired or invalid");
        }
    }

}
