package com.supermarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            // 禁用CSRF
            .csrf().disable()
            
            // 禁用Session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            
            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // 明确允许API路径
                .requestMatchers("/api/**").permitAll()
                // 允许静态资源
                .requestMatchers("/static/**", "/public/**", "/resources/**").permitAll()
                // 允许Swagger相关路径
                .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 允许所有其他请求（暂时用于测试）
                .anyRequest().permitAll()
            )
            
            // 配置CORS
            .cors();

        return http.build();
    }
}
