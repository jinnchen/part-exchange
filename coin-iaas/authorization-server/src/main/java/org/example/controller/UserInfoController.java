package org.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    private final OAuth2AuthorizationService authorizationService;

    public UserInfoController(OAuth2AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/user/info")
    public Map<String, Object> userInfo() {
        Map<String, Object> result = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        result.put("authentication", authentication);
        result.put("principal", authentication != null ? authentication.getPrincipal() : null);
        result.put("authorities", authentication != null ? authentication.getAuthorities() : null);

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            result.put("subject", jwt.getSubject());
            result.put("clientId", jwt.getClaimAsString("client_id"));
            result.put("claims", jwt.getClaims());
        }

        return result;
    }
}
