package org.example.config.jetcache;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMethodCache(basePackages = "org.example.service.impl")
public class JetCacheConfig {

}
