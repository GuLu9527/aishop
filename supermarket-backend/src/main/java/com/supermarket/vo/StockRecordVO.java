package com.supermarket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存记录视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class StockRecordVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 变动类型：1-入库，2-出库，3-盘点，4-损耗
     */
    private Integer changeType;

    /**
     * 变动类型文本
     */
    private String changeTypeText;

    /**
     * 变动数量
     */
    private Integer changeQuantity;

    /**
     * 变动前数量
     */
    private Integer beforeQuantity;

    /**
     * 变动后数量
     */
    private Integer afterQuantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 变动原因
     */
    private String reason;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 过期日期
     */
    private String expireDate;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
