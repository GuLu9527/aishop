package com.supermarket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 个体超市智能管理系统启动类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.supermarket.mapper")
@EnableAsync
@EnableScheduling
@EnableCaching
public class SupermarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermarketApplication.class, args);
        System.out.println("=================================");
        System.out.println("个体超市智能管理系统启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/doc.html");
        System.out.println("=================================");
    }
}
