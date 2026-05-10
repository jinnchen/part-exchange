package org.example.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class JwtCacheFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtCacheFilter.class);

    private final Cache<String, Jwt> jwtCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        boolean fromCache = false;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            Jwt cachedJwt = jwtCache.getIfPresent(token);

            if (cachedJwt != null) {
                fromCache = true;
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(cachedJwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT 从缓存读取, token: {}...", token.substring(0, Math.min(20, token.length())));
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (token != null && !fromCache) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                    jwtCache.put(token, jwt);
                    log.info("JWT 已存入缓存, token: {}...", token.substring(0, Math.min(20, token.length())));
                }
            }
        }
    }
}
