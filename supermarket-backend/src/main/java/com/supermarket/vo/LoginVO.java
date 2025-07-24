package com.supermarket.vo;

import lombok.Data;

import java.util.List;

/**
 * 登录响应VO
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Data
public class LoginVO {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;

    @Data
    public static class UserInfoVO {
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
         * 头像URL
         */
        private String avatar;

        /**
         * 角色列表
         */
        private List<String> roles;
    }
}
