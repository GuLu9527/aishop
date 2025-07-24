package com.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.ProductDTO;
import com.supermarket.dto.ProductQueryDTO;
import com.supermarket.dto.BatchOperationDTO;
import com.supermarket.dto.BatchOperationResultDTO;

import com.supermarket.entity.Product;
import com.supermarket.vo.ProductVO;


import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     */
    IPage<ProductVO> getProductPage(ProductQueryDTO query);

    /**
     * 根据ID查询商品详情
     */
    ProductVO getProductById(Long id);

    /**
     * 根据条码查询商品
     */
    Product getProductByBarcode(String barcode);

    /**
     * 根据商品名称查询商品
     */
    Product getProductByName(String name);

    /**
     * 添加商品
     */
    boolean addProduct(ProductDTO productDTO, Long userId);

    /**
     * 更新商品
     */
    boolean updateProduct(ProductDTO productDTO, Long userId);

    /**
     * 删除商品（逻辑删除）
     */
    boolean deleteProduct(Long id);

    /**
     * 批量删除商品
     */
    boolean deleteProducts(List<Long> ids);

    // 库存更新功能已移至 InventoryService.adjustStock() 统一管理

    /**
     * 查询低库存商品
     */
    List<ProductVO> getLowStockProducts();

    /**
     * 启用/禁用商品
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 根据条码查询商品VO
     *
     * @param barcode 商品条码
     * @return 商品VO信息
     */
    ProductVO getProductVOByBarcode(String barcode);

    /**
     * 转换Product为ProductVO
     *
     * @param product 商品实体
     * @return 商品VO
     */
    ProductVO convertToProductVO(Product product);

    /**
     * 批量操作商品
     *
     * @param batchOperationDTO 批量操作DTO
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchOperation(BatchOperationDTO batchOperationDTO, Long operatorId);

    /**
     * 批量更新商品状态（上下架）
     *
     * @param productIds 商品ID列表
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchUpdateStatus(List<Long> productIds, Integer status, Long operatorId);

    /**
     * 批量更新商品分类
     *
     * @param productIds 商品ID列表
     * @param categoryId 新分类ID
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchUpdateCategory(List<Long> productIds, Long categoryId, Long operatorId);

    /**
     * 批量更新商品价格
     *
     * @param productIds 商品ID列表
     * @param adjustmentType 价格调整类型
     * @param adjustmentValue 调整值
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchUpdatePrice(List<Long> productIds,
                                           BatchOperationDTO.PriceAdjustmentType adjustmentType,
                                           java.math.BigDecimal adjustmentValue,
                                           Long operatorId);

    /**
     * 批量删除商品（改进版）
     *
     * @param productIds 商品ID列表
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchDeleteProducts(List<Long> productIds, Long operatorId);

    // ==================== 过期管理相关方法 ====================

    /**
     * 查询临期商品
     * @param warningDays 预警天数
     * @return 临期商品列表
     */
    List<ProductVO> getExpiringProducts(Integer warningDays);

    /**
     * 查询已过期商品
     * @return 已过期商品列表
     */
    List<ProductVO> getExpiredProducts();

    /**
     * 获取商品过期统计信息
     * @return 过期统计信息
     */
    Map<String, Object> getExpirationStatistics();

    /**
     * 批量设置商品生产日期
     * @param productIds 商品ID列表
     * @param productionDate 生产日期
     * @param operatorId 操作人ID
     * @return 批量操作结果
     */
    BatchOperationResultDTO batchSetProductionDate(List<Long> productIds,
                                                  java.time.LocalDate productionDate,
                                                  Long operatorId);
}
