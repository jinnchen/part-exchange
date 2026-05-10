package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public LoginController(AuthenticationManager authenticationManager,
                          JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                  @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            Instant now = Instant.now();
            Instant expiresAt = now.plus(1, ChronoUnit.DAYS);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("http://localhost:9999")
                    .subject(username)
                    .audience(List.of("coin-api"))
                    .claim("username", username)
                    .claim("authorities", authentication.getAuthorities().toString())
                    .issuedAt(now)
                    .expiresAt(expiresAt)
                    .build();

            Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

            Map<String, Object> result = new HashMap<>();
            result.put("access_token", jwt.getTokenValue());
            result.put("token_type", "Bearer");
            result.put("expires_in", 86400);
            result.put("scope", "all");
            result.put("username", username);

            return ResponseEntity.ok(result);

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        }
    }
}
