package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.dto.ProductDTO;
import com.supermarket.dto.ProductQueryDTO;
import com.supermarket.dto.BatchOperationDTO;
import com.supermarket.dto.BatchOperationResultDTO;
import com.supermarket.entity.Product;
import com.supermarket.entity.ProductCategory;
import com.supermarket.mapper.ProductCategoryMapper;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.service.ProductService;
import com.supermarket.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final ProductCategoryMapper categoryMapper;

    @Override
    public IPage<ProductVO> getProductPage(ProductQueryDTO query) {
        Page<ProductVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return productMapper.selectProductPage(page, query);
    }




    @Override
    public ProductVO getProductById(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            return null;
        }

        // 使用统一的转换方法，确保所有计算字段都正确设置
        return convertToProductVO(product);
    }

    @Override
    public Product getProductByBarcode(String barcode) {
        return productMapper.selectByBarcode(barcode);
    }

    @Override
    public Product getProductByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return productMapper.selectByName(name.trim());
    }

    @Override
    @Transactional
    public boolean addProduct(ProductDTO productDTO, Long userId) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        product.setStatus(1); // 默认启用
        product.setCreateBy(userId);
        product.setUpdateBy(userId);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        boolean result = this.save(product);
        
        // 注意：初始库存的记录由库存管理模块(InventoryService)统一处理
        
        return result;
    }

    @Override
    @Transactional
    public boolean updateProduct(ProductDTO productDTO, Long userId) {
        Product existingProduct = this.getById(productDTO.getId());
        if (existingProduct == null) {
            return false;
        }
        
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        product.setUpdateBy(userId);
        product.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(product);
    }

    @Override
    public boolean deleteProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(0); // 逻辑删除
        product.setUpdateTime(LocalDateTime.now());
        return this.updateById(product);
    }

    @Override
    @Transactional
    public boolean deleteProducts(List<Long> ids) {
        for (Long id : ids) {
            if (!deleteProduct(id)) {
                return false;
            }
        }
        return true;
    }

    // 库存更新功能已移至 InventoryService.adjustStock() 统一管理

    @Override
    public List<ProductVO> getLowStockProducts() {
        List<Product> products = productMapper.selectLowStockProducts();
        return products.stream().map(product -> {
            ProductVO vo = convertToProductVO(product);
            vo.setIsLowStock(true); // 确保低库存标记正确
            return vo;
        }).toList();
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        return this.updateById(product);
    }

    // 库存记录功能已移至 InventoryService 统一管理

    @Override
    public ProductVO getProductVOByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }

        try {
            Product product = getProductByBarcode(barcode);
            if (product == null || product.getStatus() != 1) {
                return null;
            }

            return convertToProductVO(product);
        } catch (Exception e) {
            throw new RuntimeException("根据条码查询商品失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换Product为ProductVO
     */
    @Override
    public ProductVO convertToProductVO(Product product) {
        if (product == null) {
            return null;
        }

        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);

        // 设置分类名称
        if (product.getCategoryId() != null) {
            try {
                ProductCategory category = categoryMapper.selectById(product.getCategoryId());
                if (category != null) {
                    productVO.setCategoryName(category.getCategoryName());
                } else {
                    productVO.setCategoryName("未分类");
                }
            } catch (Exception e) {
                productVO.setCategoryName("未知分类");
            }
        } else {
            productVO.setCategoryName("未分类");
        }

        // 判断是否低库存
        if (product.getMinStock() != null) {
            productVO.setIsLowStock(product.getStockQuantity() <= product.getMinStock());
        } else {
            productVO.setIsLowStock(false);
        }

        //  关键修复：计算过期相关信息
        productVO.calculateExpirationInfo();

        return productVO;
    }

    @Override
    @Transactional
    public BatchOperationResultDTO batchOperation(BatchOperationDTO batchOperationDTO, Long operatorId) {
        BatchOperationResultDTO result = new BatchOperationResultDTO(batchOperationDTO.getProductIds().size());

        try {
            switch (batchOperationDTO.getOperationType()) {
                case UPDATE_STATUS:
                    return batchUpdateStatus(batchOperationDTO.getProductIds(),
                                           batchOperationDTO.getNewStatus(), operatorId);
                case UPDATE_CATEGORY:
                    return batchUpdateCategory(batchOperationDTO.getProductIds(),
                                             batchOperationDTO.getNewCategoryId(), operatorId);
                case UPDATE_PRICE:
                    return batchUpdatePrice(batchOperationDTO.getProductIds(),
                                          batchOperationDTO.getPriceAdjustmentType(),
                                          batchOperationDTO.getPriceAdjustmentValue(), operatorId);
                case DELETE:
                    return batchDeleteProducts(batchOperationDTO.getProductIds(), operatorId);
                default:
                    throw new IllegalArgumentException("不支持的操作类型: " + batchOperationDTO.getOperationType());
            }
        } catch (Exception e) {
            // 如果发生异常，标记所有商品为失败
            for (Long productId : batchOperationDTO.getProductIds()) {
                result.addFailure(productId, e.getMessage());
            }
            result.complete();
            return result;
        }
    }

    @Override
    @Transactional
    public BatchOperationResultDTO batchUpdateStatus(List<Long> productIds, Integer status, Long operatorId) {
        BatchOperationResultDTO result = new BatchOperationResultDTO(productIds.size());

        for (Long productId : productIds) {
            try {
                Product product = this.getById(productId);
                if (product == null) {
                    result.addFailure(productId, "商品不存在");
                    continue;
                }

                product.setStatus(status);
                product.setUpdateBy(operatorId);
                product.setUpdateTime(LocalDateTime.now());

                if (this.updateById(product)) {
                    result.addSuccess(productId);
                } else {
                    result.addFailure(productId, "更新失败");
                }
            } catch (Exception e) {
                result.addFailure(productId, "更新异常: " + e.getMessage());
            }
        }

        result.complete();
        return result;
    }

    @Override
    @Transactional
    public BatchOperationResultDTO batchUpdateCategory(List<Long> productIds, Long categoryId, Long operatorId) {
        BatchOperationResultDTO result = new BatchOperationResultDTO(productIds.size());

        // 验证分类是否存在
        if (categoryId != null) {
            ProductCategory category = categoryMapper.selectById(categoryId);
            if (category == null) {
                for (Long productId : productIds) {
                    result.addFailure(productId, "目标分类不存在");
                }
                result.complete();
                return result;
            }
        }

        for (Long productId : productIds) {
            try {
                Product product = this.getById(productId);
                if (product == null) {
                    result.addFailure(productId, "商品不存在");
                    continue;
                }

                product.setCategoryId(categoryId);
                product.setUpdateBy(operatorId);
                product.setUpdateTime(LocalDateTime.now());

                if (this.updateById(product)) {
                    result.addSuccess(productId);
                } else {
                    result.addFailure(productId, "更新失败");
                }
            } catch (Exception e) {
                result.addFailure(productId, "更新异常: " + e.getMessage());
            }
        }

        result.complete();
        return result;
    }

    @Override
    @Transactional
    public BatchOperationResultDTO batchUpdatePrice(List<Long> productIds,
                                                   BatchOperationDTO.PriceAdjustmentType adjustmentType,
                                                   java.math.BigDecimal adjustmentValue,
                                                   Long operatorId) {
        BatchOperationResultDTO result = new BatchOperationResultDTO(productIds.size());

        if (adjustmentValue == null) {
            for (Long productId : productIds) {
                result.addFailure(productId, "调整值不能为空");
            }
            result.complete();
            return result;
        }

        for (Long productId : productIds) {
            try {
                Product product = this.getById(productId);
                if (product == null) {
                    result.addFailure(productId, "商品不存在");
                    continue;
                }

                java.math.BigDecimal newPrice = calculateNewPrice(product.getSellingPrice(), adjustmentType, adjustmentValue);
                if (newPrice.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    result.addFailure(productId, "价格不能为零或负数");
                    continue;
                }

                product.setSellingPrice(newPrice);
                product.setUpdateBy(operatorId);
                product.setUpdateTime(LocalDateTime.now());

                if (this.updateById(product)) {
                    result.addSuccess(productId);
                } else {
                    result.addFailure(productId, "更新失败");
                }
            } catch (Exception e) {
                result.addFailure(productId, "更新异常: " + e.getMessage());
            }
        }

        result.complete();
        return result;
    }

    @Override
    @Transactional
    public BatchOperationResultDTO batchDeleteProducts(List<Long> productIds, Long operatorId) {
        BatchOperationResultDTO result = new BatchOperationResultDTO(productIds.size());

        for (Long productId : productIds) {
            try {
                Product product = this.getById(productId);
                if (product == null) {
                    result.addFailure(productId, "商品不存在");
                    continue;
                }

                product.setStatus(0); // 逻辑删除
                product.setUpdateBy(operatorId);
                product.setUpdateTime(LocalDateTime.now());

                if (this.updateById(product)) {
                    result.addSuccess(productId);
                } else {
                    result.addFailure(productId, "删除失败");
                }
            } catch (Exception e) {
                result.addFailure(productId, "删除异常: " + e.getMessage());
            }
        }

        result.complete();
        return result;
    }

    // ==================== 过期管理相关方法实现 ====================

    @Override
    public List<ProductVO> getExpiringProducts(Integer warningDays) {
        if (warningDays == null || warningDays <= 0) {
            warningDays = 7; // 默认7天预警
        }

        try {
            List<Product> products = productMapper.selectExpiringProducts(warningDays);
            return products.stream()
                    .map(this::convertToProductVO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询临期商品失败: warningDays={}", warningDays, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductVO> getExpiredProducts() {
        try {
            List<Product> products = productMapper.selectExpiredProducts();
            return products.stream()
                    .map(this::convertToProductVO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询已过期商品失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getExpirationStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 统计临期商品数量（7天内过期）
            Integer expiringCount = productMapper.countExpiringProducts(7);
            statistics.put("expiringCount", expiringCount != null ? expiringCount : 0);

            // 统计已过期商品数量
            Integer expiredCount = productMapper.countExpiredProducts();
            statistics.put("expiredCount", expiredCount != null ? expiredCount : 0);

            // 统计有生产日期的商品总数
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("production_date")
                       .isNotNull("shelf_life_days")
                       .gt("shelf_life_days", 0)
                       .eq("status", 1);
            Integer totalWithProductionDate = Math.toIntExact(this.count(queryWrapper));
            statistics.put("totalWithProductionDate", totalWithProductionDate);

            // 计算过期率
            if (totalWithProductionDate > 0) {
                int totalExpiring = (expiringCount != null ? expiringCount : 0) +
                                  (expiredCount != null ? expiredCount : 0);
                double expirationRate = (double) totalExpiring / totalWithProductionDate * 100;
                statistics.put("expirationRate", Math.round(expirationRate * 100.0) / 100.0);
            } else {
                statistics.put("expirationRate", 0.0);
            }

            statistics.put("success", true);
            statistics.put("message", "统计成功");

        } catch (Exception e) {
            log.error("获取商品过期统计信息失败", e);
            statistics.put("success", false);
            statistics.put("message", "统计失败：" + e.getMessage());
            statistics.put("expiringCount", 0);
            statistics.put("expiredCount", 0);
            statistics.put("totalWithProductionDate", 0);
            statistics.put("expirationRate", 0.0);
        }

        return statistics;
    }

    @Override
    @Transactional
    public BatchOperationResultDTO batchSetProductionDate(List<Long> productIds,
                                                         LocalDate productionDate,
                                                         Long operatorId) {
        BatchOperationResultDTO result = new BatchOperationResultDTO(productIds.size());

        if (productionDate == null) {
            for (Long productId : productIds) {
                result.addFailure(productId, "生产日期不能为空");
            }
            result.complete();
            return result;
        }

        if (productionDate.isAfter(LocalDate.now())) {
            for (Long productId : productIds) {
                result.addFailure(productId, "生产日期不能是未来日期");
            }
            result.complete();
            return result;
        }

        for (Long productId : productIds) {
            try {
                Product product = this.getById(productId);
                if (product == null) {
                    result.addFailure(productId, "商品不存在");
                    continue;
                }

                // 检查是否会导致商品过期
                if (product.getShelfLifeDays() != null && product.getShelfLifeDays() > 0) {
                    LocalDate expirationDate = productionDate.plusDays(product.getShelfLifeDays());
                    if (expirationDate.isBefore(LocalDate.now())) {
                        result.addFailure(productId, "设置的生产日期会导致商品已过期");
                        continue;
                    }
                }

                product.setProductionDate(productionDate);
                product.setUpdateBy(operatorId);
                product.setUpdateTime(LocalDateTime.now());

                if (this.updateById(product)) {
                    result.addSuccess(productId);
                } else {
                    result.addFailure(productId, "更新失败");
                }
            } catch (Exception e) {
                result.addFailure(productId, "更新异常: " + e.getMessage());
            }
        }

        result.complete();
        return result;
    }

    /**
     * 计算新价格
     */
    private java.math.BigDecimal calculateNewPrice(java.math.BigDecimal currentPrice,
                                                  BatchOperationDTO.PriceAdjustmentType adjustmentType,
                                                  java.math.BigDecimal adjustmentValue) {
        if (currentPrice == null) {
            currentPrice = java.math.BigDecimal.ZERO;
        }

        switch (adjustmentType) {
            case FIXED:
                return adjustmentValue;
            case PERCENTAGE:
                return currentPrice.multiply(java.math.BigDecimal.ONE.add(adjustmentValue.divide(java.math.BigDecimal.valueOf(100))));
            case AMOUNT:
                return currentPrice.add(adjustmentValue);
            default:
                throw new IllegalArgumentException("不支持的价格调整类型: " + adjustmentType);
        }
    }
}
