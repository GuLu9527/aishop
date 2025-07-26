package com.supermarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF - 使用Lambda风格
            .csrf(csrf -> csrf.disable())
            
            // 禁用Session - 使用Lambda风格
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 配置请求授权 - 使用Lambda风格
            .authorizeHttpRequests(auth -> auth
                // 放行OPTIONS请求
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 放行登录接口
                .requestMatchers("/api/auth/login").permitAll()
                // 放行注册接口
                .requestMatchers("/api/auth/register").permitAll()
                // 明确允许API路径
                .requestMatchers("/api/**").permitAll()
                // 允许静态资源
                .requestMatchers("/static/**", "/public/**", "/resources/**").permitAll()
                // 允许Swagger相关路径
                .requestMatchers(
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**"
                ).permitAll()
                // 允许所有其他请求（暂时用于测试）
                .anyRequest().permitAll()
            )
            
            // 启用CORS配置 - 使用Lambda风格
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        
        // 允许所有源
        configuration.addAllowedOriginPattern("*");
        
        // 允许所有HTTP方法（包括OPTIONS）
        configuration.addAllowedMethod("*");
        
        // 允许所有请求头
        configuration.addAllowedHeader("*");
        
        // 允许发送Cookie
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
