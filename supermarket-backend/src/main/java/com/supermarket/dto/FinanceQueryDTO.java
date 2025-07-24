package com.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录查询数据传输对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "财务记录查询数据传输对象")
public class FinanceQueryDTO {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    @Schema(description = "记录类型：1-收入，2-支出")
    private Integer recordType;

    @Schema(description = "业务类型：1-销售收入，2-采购支出，3-其他收入，4-其他支出，5-退货退款")
    private Integer businessType;

    @Schema(description = "最小金额")
    @Positive(message = "最小金额必须大于0")
    private BigDecimal minAmount;

    @Schema(description = "最大金额")
    @Positive(message = "最大金额必须大于0")
    private BigDecimal maxAmount;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "描述关键词")
    private String description;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "操作员姓名")
    private String operatorName;

    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "记录开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordStartDate;

    @Schema(description = "记录结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordEndDate;
}
