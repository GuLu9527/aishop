package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售订单实体
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@TableName("sale_order")
public class SaleOrder {

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 应收金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 实收金额
     */
    @TableField("received_amount")
    private BigDecimal receivedAmount;

    /**
     * 找零金额
     */
    @TableField("change_amount")
    private BigDecimal changeAmount;

    /**
     * 支付方式：cash-现金，card-刷卡，alipay-支付宝，wechat-微信
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 商品总数量
     */
    @TableField("total_quantity")
    private Integer totalQuantity;

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
     * 订单状态：1-已完成，2-已退款，3-部分退款
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
