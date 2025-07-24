package com.supermarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 
 * @author AI Assistant
 * @since 2024-07-22
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源处理
     * 确保API路径不会被静态资源处理器拦截
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，但排除API路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/");
        
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/resources/");
        
        // 确保API路径不被静态资源处理器处理
        // 这里不添加 /api/** 的映射，让它由Controller处理
    }
}
