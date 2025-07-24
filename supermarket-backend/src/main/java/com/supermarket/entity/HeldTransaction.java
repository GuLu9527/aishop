package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 挂单实体
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@TableName("held_transaction")
public class HeldTransaction {

    /**
     * 挂单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 挂单号
     */
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 商品数据（JSON格式存储）
     */
    @TableField("items_json")
    private String itemsJson;

    /**
     * 商品数量
     */
    @TableField("item_count")
    private Integer itemCount;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 收银员ID
     */
    @TableField("cashier_id")
    private Long cashierId;

    /**
     * 收银员姓名
     */
    @TableField("cashier_name")
    private String cashierName;

    /**
     * 收银机终端ID
     */
    @TableField("terminal_id")
    private String terminalId;

    /**
     * 状态：1-挂起，2-已取单
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
