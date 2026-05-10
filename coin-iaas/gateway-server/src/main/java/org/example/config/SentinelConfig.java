//package org.example.config;
//
//import java.io.InputStream;
//import java.util.*;
//
//import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
//import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.result.view.ViewResolver;
//import org.springframework.util.StreamUtils;
//import java.nio.charset.StandardCharsets;
//
//import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
//import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
//import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.TypeReference;
//
//@Slf4j
////@Configuration
//public class SentinelConfig {
//
//    private final List<ViewResolver> viewResolvers;
//
//    private final ServerCodecConfigurer serverCodecConfigurer;
//
//    public SentinelConfig(List<ViewResolver> viewResolvers,
//                          ServerCodecConfigurer serverCodecConfigurer) {
//        this.viewResolvers = viewResolvers;
//        this.serverCodecConfigurer = serverCodecConfigurer;
//    }
//
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
//        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
//    }
//
//    @Bean
//    @Order(-1)
//    public GlobalFilter sentinelGatewayFilter() {
//        log.info("=== SentinelGatewayFilter Bean Created ===");
//        return new SentinelGatewayFilter();
//    }
//
//    @PostConstruct
//    public void init() {
//        initFlowRules();
//        initBlockHandler();
//    }
//
//    private void initFlowRules() {
//        try {
//            ClassPathResource resource = new ClassPathResource("gw-flow.json");
//            InputStream is = resource.getInputStream();
//            String json = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
//
//            List<GatewayFlowRule> rules = JSON.parseObject(json, new TypeReference<List<GatewayFlowRule>>() {});
//
//            GatewayRuleManager.loadRules(new HashSet<>(rules));
//            log.info("=== Loaded {} Flow Rules from gw-flow.json ===", rules.size());
//            for (GatewayFlowRule rule : rules) {
//                log.info("Rule - Resource: {}, Count: {}, Interval: {}s",
//                    rule.getResource(), rule.getCount(), rule.getIntervalSec());
//            }
//        } catch (Exception e) {
//            log.error("=== Failed to load flow rules from gw-flow.json ===", e);
//        }
//    }
//
//    private void initBlockHandler() {
//        BlockRequestHandler handler = new BlockRequestHandler() {
//            @Override
//            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
//                Map<String, String> map = new HashMap<>();
//
//                map.put("errorCode", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
//                map.put("errorMessage", "TOO_MANY_REQUESTS");
//
//                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(BodyInserters.fromValue(map));
//            }
//        };
//        GatewayCallbackManager.setBlockHandler(handler);
//        log.info("setBlockHandler");
//    }
//}
