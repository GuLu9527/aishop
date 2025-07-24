package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.SaleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售订单Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface SaleOrderMapper extends BaseMapper<SaleOrder> {

    /**
     * 根据收银员ID和终端ID查询今日销售记录
     */
    @Select("SELECT * FROM sale_order WHERE cashier_id = #{cashierId} AND terminal_id = #{terminalId} " +
            "AND DATE(create_time) = CURDATE() ORDER BY create_time DESC")
    List<SaleOrder> selectTodayRecords(@Param("cashierId") Long cashierId, @Param("terminalId") String terminalId);

    /**
     * 根据时间范围查询销售记录
     */
    @Select("SELECT * FROM sale_order WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY create_time DESC")
    List<SaleOrder> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM sale_order WHERE order_no = #{orderNo}")
    SaleOrder selectByOrderNo(@Param("orderNo") String orderNo);
}
