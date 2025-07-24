package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.SaleOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 销售订单明细Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface SaleOrderItemMapper extends BaseMapper<SaleOrderItem> {

    /**
     * 根据订单ID查询订单明细
     */
    @Select("SELECT * FROM sale_order_item WHERE order_id = #{orderId}")
    List<SaleOrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 批量插入订单明细
     */
    int insertBatch(@Param("items") List<SaleOrderItem> items);
}
