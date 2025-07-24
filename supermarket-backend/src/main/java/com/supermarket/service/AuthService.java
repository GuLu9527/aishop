package com.supermarket.service;

import com.supermarket.dto.LoginDTO;
import com.supermarket.dto.RegisterDTO;
import com.supermarket.vo.LoginVO;

/**
 * 认证服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 获取用户信息
     */
    LoginVO.UserInfoVO getUserInfo(String username);
}
