package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存预警实体类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stock_alert")
public class StockAlert {

    /**
     * 预警ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;



    /**
     * 预警类型：1-低库存，2-高库存，3-临期商品
     */
    @TableField("alert_type")
    private Integer alertType;

    /**
     * 当前库存
     */
    @TableField("current_stock")
    private Integer currentStock;

    /**
     * 预警阈值
     */
    @TableField("threshold_value")
    private Integer thresholdValue;

    /**
     * 过期日期（临期商品）
     */
    @TableField("expire_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    /**
     * 预警信息
     */
    @TableField("alert_message")
    private String alertMessage;

    /**
     * 状态：1-未处理，2-已处理，0-已忽略
     */
    @TableField("status")
    private Integer status;

    /**
     * 处理人ID
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 处理时间
     */
    @TableField("handle_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

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

    /**
     * 预警类型枚举
     */
    public enum AlertType {
        LOW_STOCK(1, "低库存"),
        HIGH_STOCK(2, "高库存"),
        EXPIRING_SOON(3, "临期商品");

        private final Integer code;
        private final String description;

        AlertType(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static AlertType fromCode(Integer code) {
            for (AlertType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 预警状态枚举
     */
    public enum AlertStatus {
        IGNORED(0, "已忽略"),
        PENDING(1, "未处理"),
        HANDLED(2, "已处理");

        private final Integer code;
        private final String description;

        AlertStatus(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static AlertStatus fromCode(Integer code) {
            for (AlertStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }
}
