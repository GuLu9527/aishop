package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.HeldTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 挂单Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface HeldTransactionMapper extends BaseMapper<HeldTransaction> {

    /**
     * 根据收银员ID和终端ID查询挂单列表
     */
    @Select("SELECT * FROM held_transaction WHERE cashier_id = #{cashierId} AND terminal_id = #{terminalId} " +
            "AND status = 1 ORDER BY create_time DESC")
    List<HeldTransaction> selectByCashierAndTerminal(@Param("cashierId") Long cashierId, 
                                                    @Param("terminalId") String terminalId);

    /**
     * 根据挂单号查询挂单
     */
    @Select("SELECT * FROM held_transaction WHERE transaction_no = #{transactionNo}")
    HeldTransaction selectByTransactionNo(@Param("transactionNo") String transactionNo);
}
