package com.supermarket.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动填充处理器
 * 用于自动填充创建时间、更新时间等字段
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        
        // 自动填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 自动填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 可以根据需要添加其他字段的自动填充
        // 例如：创建人、更新人等
        // this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        // this.strictInsertFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");
        
        // 自动填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 可以根据需要添加其他字段的自动填充
        // 例如：更新人等
        // this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 获取当前用户ID（示例方法，可以根据实际情况实现）
     * 可以从SecurityContext、ThreadLocal或其他方式获取当前用户信息
     */
    private Long getCurrentUserId() {
        // TODO: 实现获取当前用户ID的逻辑
        // 例如：从Spring Security上下文中获取
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        //     UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //     // 返回用户ID
        // }
        return 1L; // 临时返回默认值
    }
}
