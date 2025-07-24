package com.supermarket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "财务记录视图对象")
public class FinanceRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "记录类型：1-收入，2-支出")
    private Integer recordType;

    @Schema(description = "记录类型文本")
    private String recordTypeText;

    @Schema(description = "业务类型：1-销售收入，2-采购支出，3-其他收入，4-其他支出，5-退货退款")
    private Integer businessType;

    @Schema(description = "业务类型文本")
    private String businessTypeText;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "关联订单号")
    private String orderNo;

    @Schema(description = "关联业务ID")
    private Long businessId;

    @Schema(description = "描述说明")
    private String description;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作员ID")
    private Long operatorId;

    @Schema(description = "操作员姓名")
    private String operatorName;

    @Schema(description = "记录日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordDate;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
