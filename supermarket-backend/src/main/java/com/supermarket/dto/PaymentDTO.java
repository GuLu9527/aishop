package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

/**
 * 支付DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class PaymentDTO {

    /**
     * 购物车商品列表
     */
    @NotEmpty(message = "购物车不能为空")
    private List<CartItemDTO> items;

    /**
     * 支付方式：cash-现金，card-刷卡，alipay-支付宝，wechat-微信
     */
    @NotNull(message = "支付方式不能为空")
    private String paymentMethod;

    /**
     * 应收金额
     */
    @NotNull(message = "应收金额不能为空")
    @DecimalMin(value = "0.01", message = "应收金额必须大于0")
    private BigDecimal totalAmount;

    /**
     * 实收金额（现金支付时必填）
     */
    private BigDecimal receivedAmount;

    /**
     * 找零金额
     */
    private BigDecimal changeAmount;

    /**
     * 收银员ID
     */
    @NotNull(message = "收银员ID不能为空")
    private Long cashierId;

    /**
     * 收银机终端ID
     */
    @NotNull(message = "收银机终端ID不能为空")
    private String terminalId;

    /**
     * 购物车商品项DTO
     */
    @Data
    public static class CartItemDTO {
        
        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        /**
         * 商品名称
         */
        @NotNull(message = "商品名称不能为空")
        private String productName;

        /**
         * 商品条码
         */
        @NotNull(message = "商品条码不能为空")
        private String barcode;

        /**
         * 销售价格
         */
        @NotNull(message = "销售价格不能为空")
        @DecimalMin(value = "0.01", message = "销售价格必须大于0")
        private BigDecimal sellingPrice;

        /**
         * 购买数量
         */
        @NotNull(message = "购买数量不能为空")
        @DecimalMin(value = "1", message = "购买数量必须大于0")
        private Integer quantity;

        /**
         * 单位
         */
        private String unit;

        /**
         * 小计金额
         */
        private BigDecimal subtotal;
    }
}
