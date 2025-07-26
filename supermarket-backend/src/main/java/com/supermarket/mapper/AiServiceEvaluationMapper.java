package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.AiServiceEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI客服评价Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface AiServiceEvaluationMapper extends BaseMapper<AiServiceEvaluation> {

    /**
     * 根据会话ID查询评价
     */
    List<AiServiceEvaluation> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据客户ID查询评价历史
     */
    List<AiServiceEvaluation> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * 计算平均满意度评分
     */
    BigDecimal calculateAverageSatisfactionScore(@Param("evaluationType") String evaluationType,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各评分等级的数量
     */
    List<Object> countByScoreLevel(@Param("evaluationType") String evaluationType,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 查询低分评价
     */
    List<AiServiceEvaluation> findLowScoreEvaluations(@Param("threshold") Integer threshold);
}