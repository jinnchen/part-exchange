package org.example.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private static final String AUTHORIZATION_KEY_PREFIX = "oauth2:authorization:";
    private static final String AUTHORIZATION_CODE_KEY_PREFIX = "oauth2:authorization:code:";
    private static final Duration TOKEN_EXPIRY = Duration.ofDays(1);

    private final RedisTemplate<String, Object> redisTemplate;
    private final RegisteredClientRepository registeredClientRepository;

    public RedisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate,
                                          RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(redisTemplate, "redisTemplate cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.redisTemplate = redisTemplate;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", authorization.getId());
        data.put("registeredClientId", authorization.getRegisteredClientId());
        data.put("principalName", authorization.getPrincipalName());
        data.put("authorizationGrantType", authorization.getAuthorizationGrantType().getValue());
        data.put("authorizedScopes", String.join(",", authorization.getAuthorizedScopes()));

        if (authorization.getAccessToken() != null) {
            OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
            data.put("accessTokenValue", accessToken.getTokenValue());
            data.put("accessTokenIssuedAt", accessToken.getIssuedAt().toString());
            data.put("accessTokenExpiresAt", accessToken.getExpiresAt().toString());
            data.put("accessTokenScopes", String.join(",", accessToken.getScopes()));
        }

        if (authorization.getRefreshToken() != null) {
            OAuth2RefreshToken refreshToken = authorization.getRefreshToken().getToken();
            data.put("refreshTokenValue", refreshToken.getTokenValue());
            data.put("refreshTokenExpiresAt", refreshToken.getExpiresAt() != null ? refreshToken.getExpiresAt().toString() : null);
        }

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCodeToken =
                authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCodeToken != null) {
            String code = authorizationCodeToken.getToken().getTokenValue();
            String codeKey = AUTHORIZATION_CODE_KEY_PREFIX + code;
            redisTemplate.opsForValue().set(codeKey, authorization.getId(), Duration.ofMinutes(10));
        }

        String key = AUTHORIZATION_KEY_PREFIX + authorization.getId();
        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, TOKEN_EXPIRY);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        String key = AUTHORIZATION_KEY_PREFIX + authorization.getId();
        redisTemplate.delete(key);

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCodeToken =
                authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCodeToken != null) {
            String code = authorizationCodeToken.getToken().getTokenValue();
            String codeKey = AUTHORIZATION_CODE_KEY_PREFIX + code;
            redisTemplate.delete(codeKey);
        }
    }

    @Override
    @Nullable
    public OAuth2Authorization findById(String id) {
        String key = AUTHORIZATION_KEY_PREFIX + id;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        if (data == null || data.isEmpty()) {
            return null;
        }
        return buildAuthorization(data);
    }

    @Override
    @Nullable
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null) {
            Set<String> keys = redisTemplate.keys(AUTHORIZATION_KEY_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                    OAuth2Authorization authorization = buildAuthorization(data);
                    if (authorization != null && hasToken(authorization, token)) {
                        return authorization;
                    }
                }
            }
            return null;
        }

        if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            Set<String> keys = redisTemplate.keys(AUTHORIZATION_KEY_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                    String storedToken = (String) data.get("refreshTokenValue");
                    if (token.equals(storedToken)) {
                        return buildAuthorization(data);
                    }
                }
            }
            return null;
        }

        if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            Set<String> keys = redisTemplate.keys(AUTHORIZATION_KEY_PREFIX + "*");
            if (keys != null) {
                for (String key : keys) {
                    Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                    String storedToken = (String) data.get("accessTokenValue");
                    if (token.equals(storedToken)) {
                        return buildAuthorization(data);
                    }
                }
            }
            return null;
        }

        if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            String codeKey = AUTHORIZATION_CODE_KEY_PREFIX + token;
            String authorizationId = (String) redisTemplate.opsForValue().get(codeKey);
            if (authorizationId != null) {
                return findById(authorizationId);
            }
        }

        return null;
    }

    private boolean hasToken(OAuth2Authorization authorization, String token) {
        if (authorization.getAccessToken() != null &&
            token.equals(authorization.getAccessToken().getToken().getTokenValue())) {
            return true;
        }
        if (authorization.getRefreshToken() != null &&
            token.equals(authorization.getRefreshToken().getToken().getTokenValue())) {
            return true;
        }
        return false;
    }

    private OAuth2Authorization buildAuthorization(Map<Object, Object> data) {
        String registeredClientId = (String) data.get("registeredClientId");
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(registeredClientId);

        if (registeredClient == null) {
            return null;
        }

        String principalName = (String) data.get("principalName");
        String grantType = (String) data.get("authorizationGrantType");
        String scopesStr = (String) data.get("authorizedScopes");
        Set<String> scopes = scopesStr != null ? new HashSet<>(Set.of(scopesStr.split(","))) : new HashSet<>();

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id((String) data.get("id"))
                .principalName(principalName)
                .authorizationGrantType(new org.springframework.security.oauth2.core.AuthorizationGrantType(grantType))
                .authorizedScopes(scopes);

        String accessTokenValue = (String) data.get("accessTokenValue");
        if (accessTokenValue != null && !accessTokenValue.isEmpty()) {
            String issuedAt = (String) data.get("accessTokenIssuedAt");
            String expiresAt = (String) data.get("accessTokenExpiresAt");
            if (issuedAt != null && expiresAt != null) {
                String tokenScopesStr = (String) data.get("accessTokenScopes");
                Set<String> tokenScopes = tokenScopesStr != null ? new HashSet<>(Set.of(tokenScopesStr.split(","))) : new HashSet<>();

                OAuth2AccessToken accessToken = new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        accessTokenValue,
                        Instant.parse(issuedAt),
                        Instant.parse(expiresAt),
                        tokenScopes);
                builder.accessToken(accessToken);
            }
        }

        String refreshTokenValue = (String) data.get("refreshTokenValue");
        if (refreshTokenValue != null && !refreshTokenValue.isEmpty()) {
            String expiresAt = (String) data.get("refreshTokenExpiresAt");
            Instant expiresAtInstant = expiresAt != null ? Instant.parse(expiresAt) : null;
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(refreshTokenValue, expiresAtInstant, expiresAtInstant != null ? expiresAtInstant.plusSeconds(2592000) : null);
            builder.refreshToken(refreshToken);
        }

        return builder.build();
    }
}
