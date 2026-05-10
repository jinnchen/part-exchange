package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "authorization-server")
public interface OAuthFeignClient {
    @PostMapping("/auth/login")
    ResponseEntity<jwtToken> getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("scope") String scope
    );
}
