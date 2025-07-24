package com.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.FinanceQueryDTO;
import com.supermarket.dto.FinanceRecordDTO;
import com.supermarket.entity.FinanceRecord;
import com.supermarket.vo.FinanceRecordVO;
import com.supermarket.vo.FinanceStatsVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 财务记录服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface FinanceRecordService extends IService<FinanceRecord> {

    /**
     * 分页查询财务记录
     *
     * @param query 查询条件
     * @return 财务记录分页数据
     */
    IPage<FinanceRecordVO> getFinanceRecordPage(FinanceQueryDTO query);

    /**
     * 根据ID查询财务记录详情
     *
     * @param id 记录ID
     * @return 财务记录详情
     */
    FinanceRecordVO getFinanceRecordById(Long id);

    /**
     * 添加财务记录
     *
     * @param financeRecordDTO 财务记录数据
     * @param operatorId       操作员ID
     * @param operatorName     操作员姓名
     * @return 是否成功
     */
    boolean addFinanceRecord(FinanceRecordDTO financeRecordDTO, Long operatorId, String operatorName);

    /**
     * 更新财务记录
     *
     * @param id               记录ID
     * @param financeRecordDTO 财务记录数据
     * @param operatorId       操作员ID
     * @param operatorName     操作员姓名
     * @return 是否成功
     */
    boolean updateFinanceRecord(Long id, FinanceRecordDTO financeRecordDTO, Long operatorId, String operatorName);

    /**
     * 删除财务记录
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean deleteFinanceRecord(Long id);

    /**
     * 批量删除财务记录
     *
     * @param ids 记录ID列表
     * @return 是否成功
     */
    boolean batchDeleteFinanceRecords(List<Long> ids);

    /**
     * 获取财务统计信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 财务统计信息
     */
    FinanceStatsVO getFinanceStats(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 获取今日财务统计
     *
     * @return 今日财务统计
     */
    FinanceStatsVO getTodayFinanceStats();

    /**
     * 获取本月财务统计
     *
     * @return 本月财务统计
     */
    FinanceStatsVO getMonthFinanceStats();

    /**
     * 记录销售收入
     *
     * @param amount       金额
     * @param orderNo      订单号
     * @param businessId   业务ID
     * @param description  描述
     * @param operatorId   操作员ID
     * @param operatorName 操作员姓名
     * @return 是否成功
     */
    boolean recordSalesIncome(BigDecimal amount, String orderNo, Long businessId, String description, Long operatorId, String operatorName);

    /**
     * 记录采购支出
     *
     * @param amount       金额
     * @param orderNo      订单号
     * @param businessId   业务ID
     * @param description  描述
     * @param operatorId   操作员ID
     * @param operatorName 操作员姓名
     * @return 是否成功
     */
    boolean recordPurchaseExpense(BigDecimal amount, String orderNo, Long businessId, String description, Long operatorId, String operatorName);

    /**
     * 记录退货退款
     *
     * @param amount       金额
     * @param orderNo      订单号
     * @param businessId   业务ID
     * @param description  描述
     * @param operatorId   操作员ID
     * @param operatorName 操作员姓名
     * @return 是否成功
     */
    boolean recordRefund(BigDecimal amount, String orderNo, Long businessId, String description, Long operatorId, String operatorName);

    /**
     * 导出财务记录数据
     *
     * @param query 查询条件
     * @return 财务记录列表
     */
    List<FinanceRecordVO> getExportData(FinanceQueryDTO query);

    /**
     * 获取财务统计概览
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 财务统计概览数据
     */
    Map<String, Object> getFinanceOverview(LocalDate startDate, LocalDate endDate);

    /**
     * 获取收支趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param period    统计周期：day/week/month
     * @return 收支趋势数据
     */
    Map<String, Object> getFinanceTrend(LocalDate startDate, LocalDate endDate, String period);

    /**
     * 获取业务类型统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 业务类型统计数据
     */
    List<Map<String, Object>> getBusinessTypeStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取月度财务报表
     *
     * @param year  年份
     * @param month 月份
     * @return 月度财务报表数据
     */
    Map<String, Object> getMonthlyReport(Integer year, Integer month);

    /**
     * 获取年度财务报表
     *
     * @param year 年份
     * @return 年度财务报表数据
     */
    Map<String, Object> getYearlyReport(Integer year);

    /**
     * 添加财务记录（自动获取当前用户信息）
     *
     * @param financeRecordDTO 财务记录数据
     * @return 是否成功
     */
    boolean addFinanceRecord(FinanceRecordDTO financeRecordDTO);

    /**
     * 更新财务记录（自动获取当前用户信息）
     *
     * @param id               记录ID
     * @param financeRecordDTO 财务记录数据
     * @return 是否成功
     */
    boolean updateFinanceRecord(Long id, FinanceRecordDTO financeRecordDTO);

    /**
     * 记录销售收入（自动获取当前用户信息）
     *
     * @param amount      金额
     * @param orderNo     订单号
     * @param businessId  业务ID
     * @param description 描述
     * @return 是否成功
     */
    boolean recordSalesIncome(BigDecimal amount, String orderNo, Long businessId, String description);

    /**
     * 记录采购支出（自动获取当前用户信息）
     *
     * @param amount      金额
     * @param orderNo     订单号
     * @param businessId  业务ID
     * @param description 描述
     * @return 是否成功
     */
    boolean recordPurchaseExpense(BigDecimal amount, String orderNo, Long businessId, String description);

    /**
     * 记录退货退款（自动获取当前用户信息）
     *
     * @param amount      金额
     * @param orderNo     订单号
     * @param businessId  业务ID
     * @param description 描述
     * @return 是否成功
     */
    boolean recordRefund(BigDecimal amount, String orderNo, Long businessId, String description);
}
