package com.tby.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000));
        return cacheManager;
    }

    @Bean
    public CacheManager redisCacheManager(RedissonClient redissonClient) {
        Map<String, org.redisson.spring.cache.CacheConfig> config = new HashMap<>();

        // TTL 1 hour (in milliseconds)
        long ttl = TimeUnit.HOURS.toMillis(1);
        long maxIdleTime = 0; // Infinite

        // Apply 1-hour TTL to our specific caches
        config.put("products", new org.redisson.spring.cache.CacheConfig(ttl, maxIdleTime));
        config.put("categories", new org.redisson.spring.cache.CacheConfig(ttl, maxIdleTime));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
