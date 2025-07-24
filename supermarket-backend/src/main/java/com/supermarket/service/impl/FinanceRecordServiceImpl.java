package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.dto.FinanceQueryDTO;
import com.supermarket.dto.FinanceRecordDTO;
import com.supermarket.entity.FinanceRecord;
import com.supermarket.mapper.FinanceRecordMapper;
import com.supermarket.service.FinanceRecordService;
import com.supermarket.utils.UserContextUtils;
import com.supermarket.vo.FinanceRecordVO;
import com.supermarket.vo.FinanceStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 财务记录服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceRecordServiceImpl extends ServiceImpl<FinanceRecordMapper, FinanceRecord> implements FinanceRecordService {

    private final FinanceRecordMapper financeRecordMapper;
    private final UserContextUtils userContextUtils;

    @Override
    public IPage<FinanceRecordVO> getFinanceRecordPage(FinanceQueryDTO query) {
        Page<FinanceRecordVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return financeRecordMapper.selectFinanceRecordPage(page, query);
    }

    @Override
    public FinanceRecordVO getFinanceRecordById(Long id) {
        return financeRecordMapper.selectFinanceRecordById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFinanceRecord(FinanceRecordDTO financeRecordDTO, Long operatorId, String operatorName) {
        try {
            FinanceRecord financeRecord = new FinanceRecord();
            BeanUtils.copyProperties(financeRecordDTO, financeRecord);
            
            // 设置操作员信息
            financeRecord.setOperatorId(operatorId);
            financeRecord.setOperatorName(operatorName);
            
            // 如果没有设置记录日期，使用当前时间
            if (financeRecord.getRecordDate() == null) {
                financeRecord.setRecordDate(LocalDateTime.now());
            }
            
            int result = financeRecordMapper.insert(financeRecord);
            log.info("添加财务记录成功，记录ID：{}，操作员：{}", financeRecord.getId(), operatorName);
            return result > 0;
        } catch (Exception e) {
            log.error("添加财务记录失败", e);
            throw new RuntimeException("添加财务记录失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFinanceRecord(Long id, FinanceRecordDTO financeRecordDTO, Long operatorId, String operatorName) {
        try {
            FinanceRecord existingRecord = financeRecordMapper.selectById(id);
            if (existingRecord == null) {
                throw new RuntimeException("财务记录不存在");
            }

            FinanceRecord financeRecord = new FinanceRecord();
            BeanUtils.copyProperties(financeRecordDTO, financeRecord);
            financeRecord.setId(id);
            
            // 保持原有的操作员信息（可选择是否更新）
            financeRecord.setOperatorId(operatorId);
            financeRecord.setOperatorName(operatorName);
            
            int result = financeRecordMapper.updateById(financeRecord);
            log.info("更新财务记录成功，记录ID：{}，操作员：{}", id, operatorName);
            return result > 0;
        } catch (Exception e) {
            log.error("更新财务记录失败，记录ID：{}", id, e);
            throw new RuntimeException("更新财务记录失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFinanceRecord(Long id) {
        try {
            int result = financeRecordMapper.deleteById(id);
            log.info("删除财务记录成功，记录ID：{}", id);
            return result > 0;
        } catch (Exception e) {
            log.error("删除财务记录失败，记录ID：{}", id, e);
            throw new RuntimeException("删除财务记录失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFinanceRecords(List<Long> ids) {
        try {
            int result = financeRecordMapper.deleteBatchIds(ids);
            log.info("批量删除财务记录成功，删除数量：{}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("批量删除财务记录失败", e);
            throw new RuntimeException("批量删除财务记录失败：" + e.getMessage());
        }
    }

    @Override
    public FinanceStatsVO getFinanceStats(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // 获取基础统计数据
            FinanceStatsVO stats = financeRecordMapper.getFinanceStats(startDate, endDate);
            if (stats == null) {
                stats = new FinanceStatsVO();
                stats.setTotalIncome(BigDecimal.ZERO);
                stats.setTotalExpense(BigDecimal.ZERO);
                stats.setNetProfit(BigDecimal.ZERO);
            }
            
            // 计算利润率
            if (stats.getTotalIncome() != null && stats.getTotalIncome().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal profitRate = stats.getNetProfit()
                    .divide(stats.getTotalIncome(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
                stats.setProfitRate(profitRate);
            } else {
                stats.setProfitRate(BigDecimal.ZERO);
            }
            
            // 获取趋势数据
            stats.setIncomeTrend(financeRecordMapper.getIncomeTrend(startDate, endDate));
            stats.setExpenseTrend(financeRecordMapper.getExpenseTrend(startDate, endDate));
            stats.setProfitTrend(financeRecordMapper.getProfitTrend(startDate, endDate));
            
            return stats;
        } catch (Exception e) {
            log.error("获取财务统计信息失败", e);
            throw new RuntimeException("获取财务统计信息失败：" + e.getMessage());
        }
    }

    @Override
    public FinanceStatsVO getTodayFinanceStats() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        
        FinanceStatsVO stats = getFinanceStats(startOfDay, endOfDay);
        stats.setTodayIncome(stats.getTotalIncome());
        stats.setTodayExpense(stats.getTotalExpense());
        stats.setTodayNetProfit(stats.getNetProfit());
        
        return stats;
    }

    @Override
    public FinanceStatsVO getMonthFinanceStats() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.now().with(LocalTime.MAX);
        
        FinanceStatsVO stats = getFinanceStats(startOfMonth, endOfMonth);
        stats.setMonthIncome(stats.getTotalIncome());
        stats.setMonthExpense(stats.getTotalExpense());
        stats.setMonthNetProfit(stats.getNetProfit());
        
        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordSalesIncome(BigDecimal amount, String orderNo, Long businessId, String description, Long operatorId, String operatorName) {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setRecordType(1); // 收入
        dto.setBusinessType(1); // 销售收入
        dto.setAmount(amount);
        dto.setOrderNo(orderNo);
        dto.setBusinessId(businessId);
        dto.setDescription(description);
        dto.setRecordDate(LocalDateTime.now());
        
        return addFinanceRecord(dto, operatorId, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordPurchaseExpense(BigDecimal amount, String orderNo, Long businessId, String description, Long operatorId, String operatorName) {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setRecordType(2); // 支出
        dto.setBusinessType(2); // 采购支出
        dto.setAmount(amount);
        dto.setOrderNo(orderNo);
        dto.setBusinessId(businessId);
        dto.setDescription(description);
        dto.setRecordDate(LocalDateTime.now());
        
        return addFinanceRecord(dto, operatorId, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordRefund(BigDecimal amount, String orderNo, Long businessId, String description, Long operatorId, String operatorName) {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setRecordType(2); // 支出
        dto.setBusinessType(5); // 退货退款
        dto.setAmount(amount);
        dto.setOrderNo(orderNo);
        dto.setBusinessId(businessId);
        dto.setDescription(description);
        dto.setRecordDate(LocalDateTime.now());
        
        return addFinanceRecord(dto, operatorId, operatorName);
    }

    @Override
    public List<FinanceRecordVO> getExportData(FinanceQueryDTO query) {
        try {
            // 设置大的页面大小以获取所有数据
            query.setPageSize(10000);
            query.setPageNum(1);

            IPage<FinanceRecordVO> page = getFinanceRecordPage(query);
            return page.getRecords();
        } catch (Exception e) {
            log.error("导出财务记录数据失败", e);
            throw new RuntimeException("导出财务记录数据失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getFinanceOverview(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            Map<String, Object> overview = new HashMap<>();

            // 获取基础统计数据
            FinanceStatsVO stats = getFinanceStats(startDateTime, endDateTime);
            overview.put("totalIncome", stats.getTotalIncome());
            overview.put("totalExpense", stats.getTotalExpense());
            overview.put("netProfit", stats.getNetProfit());
            overview.put("profitRate", stats.getProfitRate());

            // 获取记录数量统计
            Map<String, Object> recordCounts = financeRecordMapper.getRecordCounts(startDateTime, endDateTime);
            overview.put("totalRecords", recordCounts.get("totalRecords"));
            overview.put("incomeRecords", recordCounts.get("incomeRecords"));
            overview.put("expenseRecords", recordCounts.get("expenseRecords"));

            // 获取平均金额
            overview.put("avgIncomeAmount", recordCounts.get("avgIncomeAmount"));
            overview.put("avgExpenseAmount", recordCounts.get("avgExpenseAmount"));

            return overview;
        } catch (Exception e) {
            log.error("获取财务统计概览失败", e);
            throw new RuntimeException("获取财务统计概览失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getFinanceTrend(LocalDate startDate, LocalDate endDate, String period) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            Map<String, Object> trend = new HashMap<>();
            List<Map<String, Object>> trendData = new ArrayList<>();

            // 根据周期类型生成趋势数据
            switch (period.toLowerCase()) {
                case "day":
                    trendData = financeRecordMapper.getDailyTrend(startDateTime, endDateTime);
                    break;
                case "week":
                    trendData = financeRecordMapper.getWeeklyTrend(startDateTime, endDateTime);
                    break;
                case "month":
                    trendData = financeRecordMapper.getMonthlyTrend(startDateTime, endDateTime);
                    break;
                default:
                    trendData = financeRecordMapper.getDailyTrend(startDateTime, endDateTime);
            }

            trend.put("period", period);
            trend.put("data", trendData);
            trend.put("startDate", startDate);
            trend.put("endDate", endDate);

            return trend;
        } catch (Exception e) {
            log.error("获取收支趋势数据失败", e);
            throw new RuntimeException("获取收支趋势数据失败：" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getBusinessTypeStats(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            return financeRecordMapper.getBusinessTypeStats(startDateTime, endDateTime);
        } catch (Exception e) {
            log.error("获取业务类型统计失败", e);
            throw new RuntimeException("获取业务类型统计失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMonthlyReport(Integer year, Integer month) {
        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            Map<String, Object> report = new HashMap<>();

            // 获取月度概览
            Map<String, Object> overview = getFinanceOverview(startDate, endDate);
            report.put("overview", overview);

            // 获取每日趋势
            Map<String, Object> dailyTrend = getFinanceTrend(startDate, endDate, "day");
            report.put("dailyTrend", dailyTrend);

            // 获取业务类型统计
            List<Map<String, Object>> businessTypeStats = getBusinessTypeStats(startDate, endDate);
            report.put("businessTypeStats", businessTypeStats);

            // 获取月度对比（与上月对比）
            LocalDate lastMonthStart = startDate.minusMonths(1);
            LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
            Map<String, Object> lastMonthOverview = getFinanceOverview(lastMonthStart, lastMonthEnd);
            report.put("lastMonthComparison", lastMonthOverview);

            report.put("year", year);
            report.put("month", month);
            report.put("startDate", startDate);
            report.put("endDate", endDate);

            return report;
        } catch (Exception e) {
            log.error("获取月度财务报表失败", e);
            throw new RuntimeException("获取月度财务报表失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getYearlyReport(Integer year) {
        try {
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);

            Map<String, Object> report = new HashMap<>();

            // 获取年度概览
            Map<String, Object> overview = getFinanceOverview(startDate, endDate);
            report.put("overview", overview);

            // 获取月度趋势
            Map<String, Object> monthlyTrend = getFinanceTrend(startDate, endDate, "month");
            report.put("monthlyTrend", monthlyTrend);

            // 获取业务类型统计
            List<Map<String, Object>> businessTypeStats = getBusinessTypeStats(startDate, endDate);
            report.put("businessTypeStats", businessTypeStats);

            // 获取年度对比（与去年对比）
            LocalDate lastYearStart = LocalDate.of(year - 1, 1, 1);
            LocalDate lastYearEnd = LocalDate.of(year - 1, 12, 31);
            Map<String, Object> lastYearOverview = getFinanceOverview(lastYearStart, lastYearEnd);
            report.put("lastYearComparison", lastYearOverview);

            // 获取季度统计
            List<Map<String, Object>> quarterlyStats = new ArrayList<>();
            for (int quarter = 1; quarter <= 4; quarter++) {
                LocalDate quarterStart = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
                LocalDate quarterEnd = quarterStart.plusMonths(2).withDayOfMonth(quarterStart.plusMonths(2).lengthOfMonth());
                Map<String, Object> quarterOverview = getFinanceOverview(quarterStart, quarterEnd);
                quarterOverview.put("quarter", quarter);
                quarterlyStats.add(quarterOverview);
            }
            report.put("quarterlyStats", quarterlyStats);

            report.put("year", year);
            report.put("startDate", startDate);
            report.put("endDate", endDate);

            return report;
        } catch (Exception e) {
            log.error("获取年度财务报表失败", e);
            throw new RuntimeException("获取年度财务报表失败：" + e.getMessage());
        }
    }

    @Override
    public boolean addFinanceRecord(FinanceRecordDTO financeRecordDTO) {
        Object[] operatorInfo = userContextUtils.getCurrentOperatorInfo();
        Long operatorId = (Long) operatorInfo[0];
        String operatorName = (String) operatorInfo[1];
        return addFinanceRecord(financeRecordDTO, operatorId, operatorName);
    }

    @Override
    public boolean updateFinanceRecord(Long id, FinanceRecordDTO financeRecordDTO) {
        Object[] operatorInfo = userContextUtils.getCurrentOperatorInfo();
        Long operatorId = (Long) operatorInfo[0];
        String operatorName = (String) operatorInfo[1];
        return updateFinanceRecord(id, financeRecordDTO, operatorId, operatorName);
    }

    @Override
    public boolean recordSalesIncome(BigDecimal amount, String orderNo, Long businessId, String description) {
        Object[] operatorInfo = userContextUtils.getCurrentOperatorInfo();
        Long operatorId = (Long) operatorInfo[0];
        String operatorName = (String) operatorInfo[1];
        return recordSalesIncome(amount, orderNo, businessId, description, operatorId, operatorName);
    }

    @Override
    public boolean recordPurchaseExpense(BigDecimal amount, String orderNo, Long businessId, String description) {
        Object[] operatorInfo = userContextUtils.getCurrentOperatorInfo();
        Long operatorId = (Long) operatorInfo[0];
        String operatorName = (String) operatorInfo[1];
        return recordPurchaseExpense(amount, orderNo, businessId, description, operatorId, operatorName);
    }

    @Override
    public boolean recordRefund(BigDecimal amount, String orderNo, Long businessId, String description) {
        Object[] operatorInfo = userContextUtils.getCurrentOperatorInfo();
        Long operatorId = (Long) operatorInfo[0];
        String operatorName = (String) operatorInfo[1];
        return recordRefund(amount, orderNo, businessId, description, operatorId, operatorName);
    }
}
