package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.dto.LoginDTO;
import com.supermarket.dto.RegisterDTO;
import com.supermarket.service.AuthService;
import com.supermarket.utils.JwtUtils;
import com.supermarket.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 认证控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册等认证相关接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            LoginVO loginVO = authService.login(loginDTO);
            return Result.success("登录成功", loginVO);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            authService.register(registerDTO);
            return Result.success("注册成功", "注册成功");
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    @Operation(summary = "获取用户信息", description = "根据Token获取当前用户信息")
    public Result<LoginVO.UserInfoVO> getUserInfo(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return Result.unauthorized("未提供访问令牌");
            }

            if (!jwtUtils.validateToken(token)) {
                return Result.unauthorized("访问令牌无效或已过期");
            }

            String username = jwtUtils.getUsernameFromToken(token);
            LoginVO.UserInfoVO userInfo = authService.getUserInfo(username);
            return Result.success("获取用户信息成功", userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从请求中提取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
