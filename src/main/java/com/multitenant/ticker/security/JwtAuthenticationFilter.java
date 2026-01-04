package com.multitenant.ticker.security;

import com.multitenant.ticker.context.TenantContext;
import com.multitenant.ticker.entity.Tenant;
import com.multitenant.ticker.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private CustomUserDetails customUserDetails;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter() {
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // Implement JWT authentication logic here
        String token = getJWTFromRequest(request);
        if(token != null && jwtGenerator.validateToken(token)){
            String username = jwtGenerator.getUsernameFromToken(token);
            UUID tenantId = UUID.fromString(jwtGenerator.getTenantIdFromToken(token));
            TenantContext.setTenantId(tenantId);
            log.info("Set tenant context to tenantId: {} for user: {}", tenantId, username);
            UserDetails userDetails = customUserDetails.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            userDetails.getAuthorities().forEach(authority -> log.info("Has Authority: {}", authority.getAuthority()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
