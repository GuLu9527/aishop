package com.supermarket.controller;

import com.supermarket.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 用于验证路径映射
 * 
 * @author AI Assistant
 * @since 2024-07-22
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于验证系统配置的测试接口")
public class TestController {

    @GetMapping("/ping")
    @Operation(summary = "连通性测试", description = "测试API是否正常工作")
    public Result<Map<String, Object>> ping() {
        log.info("收到ping请求");
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "pong");
        result.put("timestamp", LocalDateTime.now());
        result.put("status", "API正常工作");
        
        return Result.success(result);
    }

    @DeleteMapping("/conversations/{sessionId}")
    @Operation(summary = "测试删除请求", description = "测试DELETE请求是否正常路由")
    public Result<Map<String, Object>> testDelete(@PathVariable String sessionId) {
        log.info("收到测试删除请求: {}", sessionId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "DELETE请求正常");
        result.put("sessionId", sessionId);
        result.put("timestamp", LocalDateTime.now());
        
        return Result.success(result);
    }

    @GetMapping("/conversations")
    @Operation(summary = "测试获取请求", description = "测试GET请求是否正常路由")
    public Result<Map<String, Object>> testGet() {
        log.info("收到测试获取请求");
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "GET请求正常");
        result.put("timestamp", LocalDateTime.now());
        
        return Result.success(result);
    }

    @PostMapping("/echo")
    @Operation(summary = "回声测试", description = "测试POST请求和参数传递")
    public Result<Map<String, Object>> echo(@RequestBody Map<String, Object> data) {
        log.info("收到回声测试请求: {}", data);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "回声测试成功");
        result.put("receivedData", data);
        result.put("timestamp", LocalDateTime.now());

        return Result.success(result);
    }

    @GetMapping("/validate-path/{param}")
    @Operation(summary = "路径参数验证", description = "测试路径参数是否正确传递")
    public Result<Map<String, Object>> validatePath(@PathVariable String param) {
        log.info("收到路径参数验证请求: param={}", param);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "路径参数验证成功");
        result.put("receivedParam", param);
        result.put("paramLength", param != null ? param.length() : 0);
        result.put("isEmpty", param == null || param.trim().isEmpty());
        result.put("isDot", ".".equals(param));
        result.put("timestamp", LocalDateTime.now());

        return Result.success(result);
    }
}
