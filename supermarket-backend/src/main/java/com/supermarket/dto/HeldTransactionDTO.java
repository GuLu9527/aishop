package com.supermarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

/**
 * 挂单DTO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class HeldTransactionDTO {

    /**
     * 购物车商品列表
     */
    @NotEmpty(message = "购物车不能为空")
    private List<PaymentDTO.CartItemDTO> items;

    /**
     * 商品数量
     */
    @NotNull(message = "商品数量不能为空")
    private Integer itemCount;

    /**
     * 总金额
     */
    @NotNull(message = "总金额不能为空")
    @DecimalMin(value = "0.01", message = "总金额必须大于0")
    private BigDecimal totalAmount;

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
}
