package com.supermarket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.ProductCategory;
import com.supermarket.mapper.ProductCategoryMapper;
import com.supermarket.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> 
        implements ProductCategoryService {

    private final ProductCategoryMapper categoryMapper;

    @Override
    public List<ProductCategory> getAllEnabledCategories() {
        return categoryMapper.selectAllEnabled();
    }

    @Override
    public boolean addCategory(ProductCategory category) {
        category.setStatus(1); // 默认启用
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return this.save(category);
    }

    @Override
    public boolean updateCategory(ProductCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        return this.updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        ProductCategory category = new ProductCategory();
        category.setId(id);
        category.setStatus(0); // 逻辑删除
        category.setUpdateTime(LocalDateTime.now());
        return this.updateById(category);
    }
}
