package com.supermarket.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存预警视图对象
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class StockAlertVO {

    /**
     * 预警ID
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
     * 商品条码
     */
    private String barcode;

    /**
     * 分类名称
     */
    private String categoryName;



    /**
     * 可用数量
     */
    private Integer availableQuantity;

    /**
     * 预警类型：1-低库存，2-高库存，3-临期商品
     */
    private Integer alertType;

    /**
     * 预警类型文本
     */
    private String alertTypeText;

    /**
     * 当前库存
     */
    private Integer currentStock;

    /**
     * 库存数量
     */
    private Integer stockQuantity;

    /**
     * 预警阈值
     */
    private Integer thresholdValue;

    /**
     * 保质期天数
     */
    private Integer shelfLifeDays;

    /**
     * 过期日期（临期商品）
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    /**
     * 剩余天数
     */
    private Integer remainingDays;

    /**
     * 预警信息
     */
    private String alertMessage;

    /**
     * 状态：1-未处理，2-已处理，0-已忽略
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理人姓名
     */
    private String handlerName;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handleTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 统计数量（用于统计查询）
     */
    private Integer count;

    /**
     * 紧急程度：1-低，2-中，3-高
     */
    private Integer urgencyLevel;

    /**
     * 紧急程度文本
     */
    private String urgencyText;

    /**
     * 获取预警类型文本
     */
    public String getAlertTypeText() {
        if (alertType == null) return "";
        switch (alertType) {
            case 1: return "低库存";
            case 2: return "高库存";
            case 3: return "临期商品";
            default: return "未知";
        }
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 0: return "已忽略";
            case 1: return "未处理";
            case 2: return "已处理";
            default: return "未知";
        }
    }

    /**
     * 获取紧急程度
     */
    public Integer getUrgencyLevel() {
        if (alertType == null) return 1;
        
        if (alertType == 3 && remainingDays != null) {
            // 临期商品根据剩余天数判断紧急程度
            if (remainingDays <= 1) return 3; // 高
            if (remainingDays <= 3) return 2; // 中
            return 1; // 低
        } else if (alertType == 1 && currentStock != null && thresholdValue != null) {
            // 低库存根据库存比例判断紧急程度
            double ratio = (double) currentStock / thresholdValue;
            if (ratio <= 0.2) return 3; // 高
            if (ratio <= 0.5) return 2; // 中
            return 1; // 低
        }
        
        return 1; // 默认低
    }

    /**
     * 获取紧急程度文本
     */
    public String getUrgencyText() {
        Integer level = getUrgencyLevel();
        switch (level) {
            case 1: return "低";
            case 2: return "中";
            case 3: return "高";
            default: return "低";
        }
    }
}
