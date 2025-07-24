package com.supermarket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供货商视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@Schema(description = "供货商视图对象")
public class SupplierVO {

    @Schema(description = "供货商ID")
    private Long id;

    @Schema(description = "供货商名称")
    private String supplierName;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "信用等级：1-5星")
    private Integer creditRating;

    @Schema(description = "信用等级文本")
    private String creditRatingText;

    @Schema(description = "付款条件")
    private String paymentTerms;

    @Schema(description = "供货周期（天）")
    private Integer deliveryCycle;

    @Schema(description = "状态：1-正常，0-停用")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "合作商品数量")
    private Integer productCount;

    @Schema(description = "最近采购时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastPurchaseTime;
}
