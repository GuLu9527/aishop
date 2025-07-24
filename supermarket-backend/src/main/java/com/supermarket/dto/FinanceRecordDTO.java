package com.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录数据传输对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "财务记录数据传输对象")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinanceRecordDTO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "记录类型：1-收入，2-支出")
    @NotNull(message = "记录类型不能为空")
    @Min(value = 1, message = "记录类型值错误")
    @Max(value = 2, message = "记录类型值错误")
    private Integer recordType;

    @Schema(description = "业务类型：1-销售收入，2-采购支出，3-其他收入，4-其他支出，5-退货退款")
    @NotNull(message = "业务类型不能为空")
    @Min(value = 1, message = "业务类型值错误")
    @Max(value = 5, message = "业务类型值错误")
    private Integer businessType;

    @Schema(description = "金额")
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "金额格式错误")
    private BigDecimal amount;

    @Schema(description = "关联订单号")
    @Size(max = 50, message = "订单号长度不能超过50个字符")
    private String orderNo;

    @Schema(description = "关联业务ID")
    private Long businessId;

    @Schema(description = "描述说明")
    @NotBlank(message = "描述说明不能为空")
    @Size(max = 200, message = "描述说明长度不能超过200个字符")
    private String description;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @Schema(description = "记录日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordDate;
}
