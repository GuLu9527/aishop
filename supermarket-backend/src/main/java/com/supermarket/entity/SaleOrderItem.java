package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售订单明细实体
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@TableName("sale_order_item")
public class SaleOrderItem {

    /**
     * 明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品条码
     */
    @TableField("barcode")
    private String barcode;

    /**
     * 销售价格
     */
    @TableField("selling_price")
    private BigDecimal sellingPrice;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 小计金额
     */
    @TableField("subtotal")
    private BigDecimal subtotal;



    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
