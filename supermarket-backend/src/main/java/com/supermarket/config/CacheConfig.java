package com.supermarket.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 缓存配置类
 * 基于Spring Boot缓存最佳实践
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置缓存管理器
     * 使用ConcurrentMapCacheManager作为默认实现
     * 基于ConcurrentHashMap，适合单机部署
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // 预定义缓存名称
        cacheManager.setCacheNames(
            Arrays.asList(
                "sysConfig",        // 系统配置缓存
                "sysConfigList",    // 系统配置列表缓存
                "stockAlert",       // 库存预警缓存
                "productCache",     // 商品缓存
                "categoryCache"     // 分类缓存
            )
        );
        
        // 允许运行时创建新的缓存
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
