package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.entity.StockAlert;
import com.supermarket.vo.StockAlertVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存预警Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface StockAlertMapper extends BaseMapper<StockAlert> {

    /**
     * 分页查询库存预警列表
     *
     * @param page 分页参数
     * @param alertType 预警类型
     * @param status 预警状态
     * @param productName 商品名称
     * @return 预警列表
     */
    @Select({
        "<script>",
        "SELECT sa.*, p.product_name, p.barcode, p.stock_quantity, p.shelf_life_days, ",
        "       pc.category_name, u.username as handler_name ",
        "FROM stock_alert sa ",
        "LEFT JOIN product p ON sa.product_id = p.id ",
        "LEFT JOIN product_category pc ON p.category_id = pc.id ",
        "LEFT JOIN sys_user u ON sa.handler_id = u.id ",
        "WHERE 1=1 ",
        "<if test='alertType != null'>",
        "  AND sa.alert_type = #{alertType} ",
        "</if>",
        "<if test='status != null'>",
        "  AND sa.status = #{status} ",
        "</if>",
        "<if test='productName != null and productName != \"\"'>",
        "  AND p.product_name LIKE CONCAT('%', #{productName}, '%') ",
        "</if>",
        "ORDER BY sa.create_time DESC",
        "</script>"
    })
    IPage<StockAlertVO> selectAlertPage(Page<StockAlertVO> page,
                                       @Param("alertType") Integer alertType,
                                       @Param("status") Integer status,
                                       @Param("productName") String productName);

    /**
     * 查询临期商品（优化版，基于生产日期）
     * 优先使用production_date字段，如果为空则使用create_time
     *
     * @param warningDays 预警天数
     * @return 临期商品列表
     */
    @Select({
        "SELECT p.id as product_id, p.product_name, p.barcode, p.stock_quantity, ",
        "       p.shelf_life_days, p.production_date, ",
        "       CASE ",
        "         WHEN p.production_date IS NOT NULL THEN DATE_ADD(p.production_date, INTERVAL p.shelf_life_days DAY) ",
        "         ELSE DATE_ADD(DATE(p.create_time), INTERVAL p.shelf_life_days DAY) ",
        "       END as expire_date, ",
        "       pc.category_name ",
        "FROM product p ",
        "LEFT JOIN product_category pc ON p.category_id = pc.id ",
        "WHERE p.status = 1 ",
        "  AND p.shelf_life_days IS NOT NULL ",
        "  AND p.shelf_life_days > 0 ",
        "  AND p.stock_quantity > 0 ",
        "  AND CASE ",
        "        WHEN p.production_date IS NOT NULL THEN DATE_ADD(p.production_date, INTERVAL p.shelf_life_days DAY) ",
        "        ELSE DATE_ADD(DATE(p.create_time), INTERVAL p.shelf_life_days DAY) ",
        "      END <= DATE_ADD(CURDATE(), INTERVAL #{warningDays} DAY) ",
        "  AND CASE ",
        "        WHEN p.production_date IS NOT NULL THEN DATE_ADD(p.production_date, INTERVAL p.shelf_life_days DAY) ",
        "        ELSE DATE_ADD(DATE(p.create_time), INTERVAL p.shelf_life_days DAY) ",
        "      END >= CURDATE() ",
        "ORDER BY expire_date ASC"
    })
    List<StockAlertVO> selectExpiringProducts(@Param("warningDays") Integer warningDays);

    /**
     * 查询未处理的预警数量
     *
     * @return 未处理预警数量
     */
    @Select("SELECT COUNT(*) FROM stock_alert WHERE status = 1")
    Integer countPendingAlerts();

    /**
     * 查询各类型预警数量
     *
     * @return 预警统计
     */
    @Select({
        "SELECT alert_type, COUNT(*) as count ",
        "FROM stock_alert ",
        "WHERE status = 1 ",
        "GROUP BY alert_type"
    })
    List<StockAlertVO> selectAlertStatistics();

    /**
     * 删除已处理的过期预警
     *
     * @param days 保留天数
     * @return 删除数量
     */
    @Select({
        "DELETE FROM stock_alert ",
        "WHERE status = 2 ",
        "  AND handle_time < DATE_SUB(NOW(), INTERVAL #{days} DAY)"
    })
    Integer deleteHandledAlerts(@Param("days") Integer days);
}
