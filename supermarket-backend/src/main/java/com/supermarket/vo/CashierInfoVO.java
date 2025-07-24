package com.supermarket.vo;

import lombok.Data;

/**
 * 收银员信息VO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class CashierInfoVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 状态描述
     */
    public String getStatusText() {
        return status != null && status == 1 ? "正常" : "禁用";
    }
}
