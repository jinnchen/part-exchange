package org.example.filter;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtCheckFilter.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private Environment environment;
    
    private Set<String> noRequireTokenUri = new HashSet<>();

    @PostConstruct
    public void init() {
        String noRequireUrls = environment.getProperty("no.require.urls", "/admin/login,/admin/register");
        log.info("no.require.urls config: {}", noRequireUrls);
        if (StringUtils.hasText(noRequireUrls)) {
            noRequireTokenUri = new HashSet<>(Arrays.asList(noRequireUrls.split(",")));
        }
        log.info("noRequireTokenUri: {}", noRequireTokenUri);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String remoteAddr = exchange.getRequest().getRemoteAddress() != null 
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
            : "unknown";
        log.info("收到请求: path={}, remoteAddr={}", path, remoteAddr);
        log.info("Request path: {}, noRequireTokenUri: {}", path, noRequireTokenUri);
        
        try {
            if (!isRequireToken(exchange)) {
                log.info("Path {} is in whitelist, skipping token check, forwarding to backend", path);
                return chain.filter(exchange);
            }

            String token = getUserToken(exchange);
            if (StringUtils.isEmpty(token)) {
                log.info("Token为空，检查是否需要验证: path={}, noRequireTokenUri={}", path, noRequireTokenUri);
                return buildNoAuthorizationResult(exchange);
            }
            Boolean hasKey = redisTemplate.hasKey(token);
            if (hasKey) {
                log.info("Token验证通过，转发到后端: path={}", path);
                return chain.filter(exchange);
            }
            log.warn("Token不存在于Redis: {}", token);
        } catch (Exception e) {
            log.error("处理请求异常: path={}, error={}", path, e.getMessage(), e);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> buildNoAuthorizationResult(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "NO AUTHORIZED");
        jsonObject.put("errorMsg", "token is null or error");
        DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
        return response.writeWith(Flux.just(buffer));
    }

    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token ==  null ? null : token.replace("Bearer ", "");

    }

    private boolean isRequireToken(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return !noRequireTokenUri.contains(path);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
