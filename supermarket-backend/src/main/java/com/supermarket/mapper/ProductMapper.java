package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.dto.ProductQueryDTO;
import com.supermarket.entity.Product;
import com.supermarket.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询商品列表
     */
    IPage<ProductVO> selectProductPage(Page<ProductVO> page, @Param("query") ProductQueryDTO query);

    /**
     * 根据条码查询商品
     */
    @Select("SELECT * FROM product WHERE barcode = #{barcode} AND status = 1")
    Product selectByBarcode(@Param("barcode") String barcode);

    /**
     * 根据商品名称查询商品
     */
    @Select("SELECT * FROM product WHERE product_name = #{name} AND status = 1 LIMIT 1")
    Product selectByName(@Param("name") String name);

    /**
     * 查询低库存商品
     */
    @Select("SELECT * FROM product WHERE stock_quantity <= min_stock AND status = 1")
    List<Product> selectLowStockProducts();

    /**
     * 统计商品总数
     */
    @Select("SELECT COUNT(*) FROM product WHERE status = 1")
    Integer countTotalProducts();

    /**
     * 统计低库存商品数量
     */
    @Select("SELECT COUNT(*) FROM product WHERE stock_quantity <= min_stock AND status = 1")
    Integer countLowStockProducts();

    /**
     * 获取今日销售额
     */
    @Select("SELECT COALESCE(SUM(oi.quantity * oi.selling_price), 0) " +
            "FROM sale_order o " +
            "JOIN sale_order_item oi ON o.id = oi.order_id " +
            "WHERE DATE(o.create_time) = CURDATE() AND o.status = 1")
    BigDecimal getTodaySales();

    /**
     * 统计即将过期商品数量（7天内过期）- 优化版，基于生产日期
     */
    @Select("SELECT COUNT(*) FROM product " +
            "WHERE status = 1 " +
            "  AND production_date IS NOT NULL " +
            "  AND shelf_life_days IS NOT NULL " +
            "  AND shelf_life_days > 0 " +
            "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) <= DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
            "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) >= CURDATE()")
    Integer countExpiringSoonProducts();

    /**
     * 查询临期商品
     * @param warningDays 预警天数
     * @return 临期商品列表
     */
    @Select({
        "SELECT * FROM product ",
        "WHERE production_date IS NOT NULL ",
        "  AND shelf_life_days IS NOT NULL ",
        "  AND shelf_life_days > 0 ",
        "  AND status = 1 ",
        "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) <= DATE_ADD(CURDATE(), INTERVAL #{warningDays} DAY) ",
        "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) >= CURDATE() ",
        "ORDER BY DATE_ADD(production_date, INTERVAL shelf_life_days DAY) ASC"
    })
    List<Product> selectExpiringProducts(@Param("warningDays") Integer warningDays);

    /**
     * 查询已过期商品
     * @return 已过期商品列表
     */
    @Select({
        "SELECT * FROM product ",
        "WHERE production_date IS NOT NULL ",
        "  AND shelf_life_days IS NOT NULL ",
        "  AND shelf_life_days > 0 ",
        "  AND status = 1 ",
        "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) < CURDATE() ",
        "ORDER BY DATE_ADD(production_date, INTERVAL shelf_life_days DAY) DESC"
    })
    List<Product> selectExpiredProducts();

    /**
     * 统计临期商品数量
     * @param warningDays 预警天数
     * @return 临期商品数量
     */
    @Select({
        "SELECT COUNT(*) FROM product ",
        "WHERE production_date IS NOT NULL ",
        "  AND shelf_life_days IS NOT NULL ",
        "  AND shelf_life_days > 0 ",
        "  AND status = 1 ",
        "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) <= DATE_ADD(CURDATE(), INTERVAL #{warningDays} DAY) ",
        "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) >= CURDATE()"
    })
    Integer countExpiringProducts(@Param("warningDays") Integer warningDays);

    /**
     * 统计已过期商品数量
     * @return 已过期商品数量
     */
    @Select({
        "SELECT COUNT(*) FROM product ",
        "WHERE production_date IS NOT NULL ",
        "  AND shelf_life_days IS NOT NULL ",
        "  AND shelf_life_days > 0 ",
        "  AND status = 1 ",
        "  AND DATE_ADD(production_date, INTERVAL shelf_life_days DAY) < CURDATE()"
    })
    Integer countExpiredProducts();
}
