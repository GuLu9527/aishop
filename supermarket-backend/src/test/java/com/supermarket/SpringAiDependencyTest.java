package com.supermarket;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.supermarket.entity.Product;
import com.supermarket.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest; // 关键注解

// 添加 @SpringBootTest 注解，启动 Spring 容器
@SpringBootTest
public class SpringAiDependencyTest {

    @Autowired
    private ProductService productService;

    @Test
    public void test(){
        // 现在 productService 会被 Spring 注入，不再为 null
        QueryChainWrapper<Product> query = productService.query();
        System.out.println(query.list());
    }

}