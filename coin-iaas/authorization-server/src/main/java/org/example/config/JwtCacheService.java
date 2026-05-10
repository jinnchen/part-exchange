package org.example.config;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtCacheService {

    @Cacheable(value = "jwtAuth", key = "#token", unless = "#result == null")
    public Authentication getCachedAuthentication(String token) {
        return null;
    }

    public static Authentication buildAuthentication(Jwt jwt) {
        return new JwtAuthenticationToken(jwt);
    }
}
