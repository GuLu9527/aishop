package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.entity.ProductCategory;
import com.supermarket.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Tag(name = "商品分类管理", description = "商品分类管理相关接口")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @Operation(summary = "查询所有启用的分类")
    @GetMapping
    public Result<List<ProductCategory>> getAllCategories() {
        try {
            List<ProductCategory> categories = categoryService.getAllEnabledCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error("查询分类列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{id}")
    public Result<ProductCategory> getCategoryById(@PathVariable Long id) {
        try {
            ProductCategory category = categoryService.getById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            return Result.error("查询分类失败：" + e.getMessage());
        }
    }

    @Operation(summary = "添加分类")
    @PostMapping
    public Result<String> addCategory(@RequestBody ProductCategory category) {
        try {
            boolean success = categoryService.addCategory(category);
            if (success) {
                return Result.success("添加分类成功");
            } else {
                return Result.error("添加分类失败");
            }
        } catch (Exception e) {
            return Result.error("添加分类失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody ProductCategory category) {
        try {
            category.setId(id);
            boolean success = categoryService.updateCategory(category);
            if (success) {
                return Result.success("更新分类成功");
            } else {
                return Result.error("更新分类失败");
            }
        } catch (Exception e) {
            return Result.error("更新分类失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            if (success) {
                return Result.success("删除分类成功");
            } else {
                return Result.error("删除分类失败");
            }
        } catch (Exception e) {
            return Result.error("删除分类失败：" + e.getMessage());
        }
    }
}
