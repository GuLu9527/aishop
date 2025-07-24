package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 供货商实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("supplier")
public class Supplier {

    /**
     * 供货商ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 供货商名称
     */
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 信用等级：1-5星
     */
    @TableField("credit_rating")
    private Integer creditRating;

    /**
     * 付款条件
     */
    @TableField("payment_terms")
    private String paymentTerms;

    /**
     * 供货周期（天）
     */
    @TableField("delivery_cycle")
    private Integer deliveryCycle;

    /**
     * 状态：1-正常，0-停用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
