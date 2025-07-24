package com.supermarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 查询所有启用的分类
     */
    List<ProductCategory> getAllEnabledCategories();

    /**
     * 添加分类
     */
    boolean addCategory(ProductCategory category);

    /**
     * 更新分类
     */
    boolean updateCategory(ProductCategory category);

    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);
}
