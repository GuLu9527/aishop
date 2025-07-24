package com.supermarket.utils;

import com.supermarket.entity.SysUser;
import com.supermarket.mapper.SysUserMapper;
import com.supermarket.exception.AuthenticationException;
import com.supermarket.exception.UserNotActiveException;
import com.supermarket.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类 - 用于管理和获取当前用户会话信息
 * 
 * 主要功能：
 * 1. 用户认证状态管理
 * 2. 当前用户信息获取
 * 3. JWT Token处理
 * 4. 用户会话验证
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextUtils {

    private final JwtUtils jwtUtils;
    private final SysUserMapper sysUserMapper;

    // 用户状态常量
    private static final int USER_STATUS_ACTIVE = 1;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 获取当前登录用户信息
     * 此方法会进行完整的用户验证流程，包括：
     * 1. 验证请求上下文
     * 2. 验证并解析JWT令牌
     * 3. 检查用户状态
     *
     * @return 当前用户信息
     * @throws AuthenticationException 当认证失败时
     * @throws UserNotFoundException 当用户不存在时
     * @throws UserNotActiveException 当用户被禁用时
     */
    public SysUser getCurrentUser() {
        try {
            // 1. 获取并验证请求上下文
            HttpServletRequest request = getRequestOrThrow();
            
            // 2. 获取并验证Token
            String token = extractAndValidateToken(request);
            
            // 3. 获取用户信息
            String username = extractUsernameFromToken(token);
            
            // 4. 验证用户状态
            return validateAndGetUser(username);
            
        } catch (Exception e) {
            if (e instanceof AuthenticationException || 
                e instanceof UserNotFoundException || 
                e instanceof UserNotActiveException) {
                throw e;
            }
            log.error("获取当前用户信息时发生未预期的错误", e);
            throw new AuthenticationException("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     * @throws AuthenticationException 当用户未登录或认证失败时
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 当前用户名
     * @throws AuthenticationException 当用户未登录或认证失败时
     */
    public String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * 获取当前登录用户真实姓名
     *
     * @return 当前用户真实姓名
     * @throws AuthenticationException 当用户未登录或认证失败时
     */
    public String getCurrentUserRealName() {
        return getCurrentUser().getRealName();
    }

    /**
     * 获取当前登录用户的操作员信息
     * 如果用户未登录或认证失败，返回默认系统用户信息
     *
     * @return 操作员信息数组 [用户ID, 用户真实姓名]
     */
    public Object[] getCurrentOperatorInfo() {
        try {
            SysUser user = getCurrentUser();
            return new Object[]{user.getId(), user.getRealName()};
        } catch (Exception e) {
            log.warn("获取当前用户信息失败，使用默认系统用户: {}", e.getMessage());
            return new Object[]{1L, "系统管理员"};
        }
    }

    /**
     * 检查当前用户是否已登录
     * 此方法不会抛出异常，适用于需要简单判断登录状态的场景
     *
     * @return 是否已登录
     */
    public boolean isCurrentUserLoggedIn() {
        try {
            return getCurrentUser() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取请求上下文，如果不存在则抛出异常
     *
     * @return HTTP请求对象
     * @throws AuthenticationException 当无法获取请求上下文时
     */
    private HttpServletRequest getRequestOrThrow() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new AuthenticationException("无法获取请求上下文");
        }
        return attributes.getRequest();
    }

    /**
     * 从请求中提取并验证JWT令牌
     *
     * @param request HTTP请求
     * @return 验证通过的JWT令牌
     * @throws AuthenticationException 当令牌无效或不存在时
     */
    private String extractAndValidateToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("未提供有效的认证令牌");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!jwtUtils.validateToken(token)) {
            throw new AuthenticationException("认证令牌已过期或无效");
        }

        return token;
    }

    /**
     * 从Token中提取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     * @throws AuthenticationException 当无法从令牌中获取用户名时
     */
    private String extractUsernameFromToken(String token) {
        String username = jwtUtils.getUsernameFromToken(token);
        if (!StringUtils.hasText(username)) {
            throw new AuthenticationException("无法从认证令牌中获取用户信息");
        }
        return username;
    }

    /**
     * 验证用户状态并返回用户信息
     *
     * @param username 用户名
     * @return 用户信息
     * @throws UserNotFoundException 当用户不存在时
     * @throws UserNotActiveException 当用户被禁用时
     */
    private SysUser validateAndGetUser(String username) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() != USER_STATUS_ACTIVE) {
            throw new UserNotActiveException("用户已被禁用: " + username);
        }

        return user;
    }

    /**
     * 要求当前用户必须已登录，否则抛出异常
     * 此方法为getCurrentUser的别名，保持向后兼容性
     *
     * @return 当前用户信息
     * @throws AuthenticationException 当用户未登录时
     */
    public SysUser requireCurrentUser() {
        return getCurrentUser();
    }

    /**
     * 要求当前用户必须已登录，否则抛出异常
     * 此方法为getCurrentUserId的别名，保持向后兼容性
     *
     * @return 当前用户ID
     * @throws AuthenticationException 当用户未登录时
     */
    public Long requireCurrentUserId() {
        return getCurrentUserId();
    }
}
