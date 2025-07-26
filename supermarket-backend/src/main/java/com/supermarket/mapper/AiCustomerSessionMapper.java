package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.AiCustomerSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI客服会话Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface AiCustomerSessionMapper extends BaseMapper<AiCustomerSession> {

    /**
     * 根据客户ID查询活跃会话
     */
    AiCustomerSession findActiveSessionByCustomerId(@Param("customerId") Long customerId);

    /**
     * 根据会话ID查询会话信息
     */
    AiCustomerSession findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 查询客户的历史会话列表
     */
    List<AiCustomerSession> findHistoryByCustomerId(@Param("customerId") Long customerId);

    /**
     * 查询需要人工介入的会话
     */
    List<AiCustomerSession> findSessionsNeedingHumanIntervention();

    /**
     * 统计指定时间范围内的会话数量
     */
    Long countSessionsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}