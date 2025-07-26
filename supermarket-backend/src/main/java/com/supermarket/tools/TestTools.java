package com.supermarket.tools;


import com.supermarket.entity.Product;
import com.supermarket.service.ProductService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;



@Component
public class TestTools {

    @Autowired
    private ProductService productService;


    @Tool(description = "查询商品列表")
    public List<Product> getProducts() {
        return productService.query().list();
    }
}
