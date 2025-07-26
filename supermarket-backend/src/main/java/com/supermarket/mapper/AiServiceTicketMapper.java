package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.AiServiceTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI客服工单Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface AiServiceTicketMapper extends BaseMapper<AiServiceTicket> {

    /**
     * 根据工单编号查询
     */
    AiServiceTicket findByTicketNo(@Param("ticketNo") String ticketNo);

    /**
     * 根据会话ID查询工单
     */
    List<AiServiceTicket> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据客户ID查询工单
     */
    List<AiServiceTicket> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * 根据状态查询工单
     */
    List<AiServiceTicket> findByStatus(@Param("status") String status);

    /**
     * 根据分配人查询工单
     */
    List<AiServiceTicket> findByAssignedTo(@Param("assignedTo") Long assignedTo);

    /**
     * 查询待分配的工单
     */
    List<AiServiceTicket> findUnassignedTickets();

    /**
     * 统计指定时间范围内的工单数量
     */
    Long countTicketsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态工单数量
     */
    List<Object> countByStatus();
}