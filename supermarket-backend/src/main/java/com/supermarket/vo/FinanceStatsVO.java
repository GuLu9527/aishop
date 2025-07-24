package com.supermarket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 财务统计视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "财务统计视图对象")
public class FinanceStatsVO {

    @Schema(description = "总收入")
    private BigDecimal totalIncome;

    @Schema(description = "总支出")
    private BigDecimal totalExpense;

    @Schema(description = "净利润")
    private BigDecimal netProfit;

    @Schema(description = "利润率")
    private BigDecimal profitRate;

    @Schema(description = "今日收入")
    private BigDecimal todayIncome;

    @Schema(description = "今日支出")
    private BigDecimal todayExpense;

    @Schema(description = "今日净利润")
    private BigDecimal todayNetProfit;

    @Schema(description = "本月收入")
    private BigDecimal monthIncome;

    @Schema(description = "本月支出")
    private BigDecimal monthExpense;

    @Schema(description = "本月净利润")
    private BigDecimal monthNetProfit;

    @Schema(description = "销售收入")
    private BigDecimal salesIncome;

    @Schema(description = "采购支出")
    private BigDecimal purchaseExpense;

    @Schema(description = "其他收入")
    private BigDecimal otherIncome;

    @Schema(description = "其他支出")
    private BigDecimal otherExpense;

    @Schema(description = "退货退款")
    private BigDecimal refundAmount;

    @Schema(description = "收入趋势数据")
    private List<TrendData> incomeTrend;

    @Schema(description = "支出趋势数据")
    private List<TrendData> expenseTrend;

    @Schema(description = "利润趋势数据")
    private List<TrendData> profitTrend;

    /**
     * 趋势数据内部类
     */
    @Data
    @Schema(description = "趋势数据")
    public static class TrendData {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "金额")
        private BigDecimal amount;
    }
}
