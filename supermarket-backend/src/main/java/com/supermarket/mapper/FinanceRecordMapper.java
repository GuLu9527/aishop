package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.dto.FinanceQueryDTO;
import com.supermarket.entity.FinanceRecord;
import com.supermarket.vo.FinanceRecordVO;
import com.supermarket.vo.FinanceStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 财务记录Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface FinanceRecordMapper extends BaseMapper<FinanceRecord> {

    /**
     * 分页查询财务记录
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 财务记录分页数据
     */
    IPage<FinanceRecordVO> selectFinanceRecordPage(Page<FinanceRecordVO> page, @Param("query") FinanceQueryDTO query);

    /**
     * 根据ID查询财务记录详情
     *
     * @param id 记录ID
     * @return 财务记录详情
     */
    FinanceRecordVO selectFinanceRecordById(@Param("id") Long id);

    /**
     * 获取财务统计信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 财务统计信息
     */
    FinanceStatsVO getFinanceStats(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取收入趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 收入趋势数据
     */
    List<FinanceStatsVO.TrendData> getIncomeTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取支出趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 支出趋势数据
     */
    List<FinanceStatsVO.TrendData> getExpenseTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取利润趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 利润趋势数据
     */
    List<FinanceStatsVO.TrendData> getProfitTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根据业务类型统计金额
     *
     * @param recordType   记录类型
     * @param businessType 业务类型
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @return 统计金额
     */
    BigDecimal sumAmountByType(@Param("recordType") Integer recordType, 
                               @Param("businessType") Integer businessType,
                               @Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);

    /**
     * 根据记录类型统计金额
     *
     * @param recordType 记录类型
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 统计金额
     */
    BigDecimal sumAmountByRecordType(@Param("recordType") Integer recordType,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * 获取记录数量统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 记录数量统计
     */
    Map<String, Object> getRecordCounts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取每日趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 每日趋势数据
     */
    List<Map<String, Object>> getDailyTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取每周趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 每周趋势数据
     */
    List<Map<String, Object>> getWeeklyTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取每月趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 每月趋势数据
     */
    List<Map<String, Object>> getMonthlyTrend(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 获取业务类型统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 业务类型统计数据
     */
    List<Map<String, Object>> getBusinessTypeStats(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
