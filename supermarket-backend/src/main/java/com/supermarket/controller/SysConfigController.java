package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.entity.SysConfig;
import com.supermarket.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/sys-config")
@Tag(name = "系统配置管理", description = "系统配置相关接口")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @Operation(summary = "获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        try {
            String value = sysConfigService.getConfigValue(configKey);
            return Result.success(value);
        } catch (Exception e) {
            log.error("获取配置值失败: configKey={}", configKey, e);
            return Result.error("获取配置值失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取配置信息")
    @GetMapping("/{configKey}")
    public Result<SysConfig> getConfig(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        try {
            SysConfig config = sysConfigService.getConfig(configKey);
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取配置信息失败: configKey={}", configKey, e);
            return Result.error("获取配置信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取所有配置")
    @GetMapping("/list")
    public Result<List<SysConfig>> getAllConfigs() {
        try {
            List<SysConfig> configs = sysConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取所有配置失败", e);
            return Result.error("获取所有配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据前缀获取配置")
    @GetMapping("/prefix/{keyPrefix}")
    public Result<List<SysConfig>> getConfigsByPrefix(
            @Parameter(description = "配置键前缀") @PathVariable String keyPrefix) {
        try {
            List<SysConfig> configs = sysConfigService.getConfigsByPrefix(keyPrefix);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("根据前缀获取配置失败: keyPrefix={}", keyPrefix, e);
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取配置映射")
    @GetMapping("/map")
    public Result<Map<String, String>> getConfigMap(
            @Parameter(description = "配置键前缀（可选）") @RequestParam(required = false) String keyPrefix) {
        try {
            Map<String, String> configMap;
            if (keyPrefix != null && !keyPrefix.trim().isEmpty()) {
                configMap = sysConfigService.getConfigMapByPrefix(keyPrefix);
            } else {
                configMap = sysConfigService.getConfigMap();
            }
            return Result.success(configMap);
        } catch (Exception e) {
            log.error("获取配置映射失败: keyPrefix={}", keyPrefix, e);
            return Result.error("获取配置映射失败：" + e.getMessage());
        }
    }

    @Operation(summary = "设置配置值")
    @PutMapping("/{configKey}")
    public Result<String> setConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "配置值") @RequestParam String configValue,
            @Parameter(description = "配置描述") @RequestParam(required = false) String description) {
        try {
            boolean success = sysConfigService.setConfigValue(configKey, configValue, description);
            if (success) {
                return Result.success("配置设置成功");
            } else {
                return Result.error("配置设置失败");
            }
        } catch (Exception e) {
            log.error("设置配置值失败: configKey={}, configValue={}", configKey, configValue, e);
            return Result.error("设置配置值失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量设置配置")
    @PutMapping("/batch")
    public Result<String> batchSetConfigs(@RequestBody Map<String, String> configMap) {
        try {
            int successCount = sysConfigService.batchSetConfigs(configMap);
            return Result.success(String.format("批量设置完成，成功设置 %d 个配置", successCount));
        } catch (Exception e) {
            log.error("批量设置配置失败", e);
            return Result.error("批量设置配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{configKey}")
    public Result<String> deleteConfig(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        try {
            boolean success = sysConfigService.deleteConfig(configKey);
            if (success) {
                return Result.success("配置删除成功");
            } else {
                return Result.error("配置删除失败");
            }
        } catch (Exception e) {
            log.error("删除配置失败: configKey={}", configKey, e);
            return Result.error("删除配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查配置是否存在")
    @GetMapping("/exists/{configKey}")
    public Result<Boolean> existsConfig(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        try {
            boolean exists = sysConfigService.existsConfig(configKey);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查配置是否存在失败: configKey={}", configKey, e);
            return Result.error("检查配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "刷新配置缓存")
    @PostMapping("/refresh-cache")
    public Result<String> refreshCache() {
        try {
            sysConfigService.refreshCache();
            return Result.success("配置缓存刷新成功");
        } catch (Exception e) {
            log.error("刷新配置缓存失败", e);
            return Result.error("刷新配置缓存失败：" + e.getMessage());
        }
    }
}
