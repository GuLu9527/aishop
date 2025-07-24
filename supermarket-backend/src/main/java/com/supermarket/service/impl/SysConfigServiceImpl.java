package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.SysConfig;
import com.supermarket.mapper.SysConfigMapper;
import com.supermarket.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现类
 * 基于Spring Boot缓存最佳实践实现
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 本地缓存，用于高频访问的配置项
     * 基于ConcurrentHashMap实现线程安全
     */
    private final Map<String, String> localCache = new ConcurrentHashMap<>();

    /**
     * 缓存初始化标志
     */
    private volatile boolean cacheInitialized = false;

    /**
     * 初始化缓存
     * 在服务启动时加载所有配置到本地缓存
     */
    @PostConstruct
    public void initCache() {
        try {
            log.info("开始初始化系统配置缓存");
            refreshCache();
            log.info("系统配置缓存初始化完成，共加载 {} 个配置项", localCache.size());
        } catch (Exception e) {
            log.error("初始化系统配置缓存失败", e);
        }
    }

    @Override
    @Cacheable(value = "sysConfig", key = "#configKey", unless = "#result == null")
    public String getConfigValue(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }

        // 优先从本地缓存获取
        if (cacheInitialized && localCache.containsKey(configKey)) {
            return localCache.get(configKey);
        }

        // 从数据库获取
        try {
            String value = sysConfigMapper.selectValueByKey(configKey);
            if (value != null && cacheInitialized) {
                localCache.put(configKey, value);
            }
            return value;
        } catch (Exception e) {
            log.error("获取配置值失败: configKey={}", configKey, e);
            return null;
        }
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    public Integer getIntValue(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的整数: configKey={}, value={}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Boolean getBooleanValue(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        
        String trimmedValue = value.trim().toLowerCase();
        if ("true".equals(trimmedValue) || "1".equals(trimmedValue) || "yes".equals(trimmedValue)) {
            return true;
        } else if ("false".equals(trimmedValue) || "0".equals(trimmedValue) || "no".equals(trimmedValue)) {
            return false;
        } else {
            log.warn("配置值不是有效的布尔值: configKey={}, value={}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Long getLongValue(String configKey, Long defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的长整数: configKey={}, value={}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    public Double getDoubleValue(String configKey, Double defaultValue) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值不是有效的双精度数: configKey={}, value={}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "sysConfig", key = "#configKey")
    public boolean setConfigValue(String configKey, String configValue) {
        return setConfigValue(configKey, configValue, null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "sysConfig", key = "#configKey")
    public boolean setConfigValue(String configKey, String configValue, String description) {
        if (!StringUtils.hasText(configKey)) {
            log.warn("配置键不能为空");
            return false;
        }

        try {
            SysConfig existingConfig = sysConfigMapper.selectByKey(configKey);
            
            if (existingConfig != null) {
                // 更新现有配置
                existingConfig.setConfigValue(configValue);
                if (StringUtils.hasText(description)) {
                    existingConfig.setDescription(description);
                }
                existingConfig.setUpdateTime(LocalDateTime.now());
                
                boolean success = this.updateById(existingConfig);
                if (success && cacheInitialized) {
                    localCache.put(configKey, configValue);
                }
                return success;
            } else {
                // 创建新配置
                SysConfig newConfig = new SysConfig();
                newConfig.setConfigKey(configKey);
                newConfig.setConfigValue(configValue);
                newConfig.setDescription(description);
                newConfig.setCreateTime(LocalDateTime.now());
                newConfig.setUpdateTime(LocalDateTime.now());
                
                boolean success = this.save(newConfig);
                if (success && cacheInitialized) {
                    localCache.put(configKey, configValue);
                }
                return success;
            }
        } catch (Exception e) {
            log.error("设置配置值失败: configKey={}, configValue={}", configKey, configValue, e);
            return false;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "sysConfig", key = "#configKey")
    public boolean deleteConfig(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }

        try {
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, configKey);
            
            boolean success = this.remove(wrapper);
            if (success && cacheInitialized) {
                localCache.remove(configKey);
            }
            return success;
        } catch (Exception e) {
            log.error("删除配置失败: configKey={}", configKey, e);
            return false;
        }
    }

    @Override
    @Cacheable(value = "sysConfigList", key = "'allConfigs'")
    public List<SysConfig> getAllConfigs() {
        try {
            return sysConfigMapper.selectAllConfigs();
        } catch (Exception e) {
            log.error("获取所有配置失败", e);
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "sysConfigList", key = "#keyPrefix")
    public List<SysConfig> getConfigsByPrefix(String keyPrefix) {
        if (!StringUtils.hasText(keyPrefix)) {
            return List.of();
        }

        try {
            return sysConfigMapper.selectByKeyPrefix(keyPrefix);
        } catch (Exception e) {
            log.error("根据前缀获取配置失败: keyPrefix={}", keyPrefix, e);
            return List.of();
        }
    }

    @Override
    public Map<String, String> getConfigMap() {
        if (cacheInitialized) {
            return new HashMap<>(localCache);
        }

        List<SysConfig> configs = getAllConfigs();
        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    @Override
    public Map<String, String> getConfigMapByPrefix(String keyPrefix) {
        List<SysConfig> configs = getConfigsByPrefix(keyPrefix);
        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    @Override
    public boolean existsConfig(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }

        if (cacheInitialized) {
            return localCache.containsKey(configKey);
        }

        try {
            return sysConfigMapper.countByKey(configKey) > 0;
        } catch (Exception e) {
            log.error("检查配置是否存在失败: configKey={}", configKey, e);
            return false;
        }
    }

    @Override
    @CacheEvict(value = {"sysConfig", "sysConfigList"}, allEntries = true)
    public void refreshCache() {
        try {
            log.info("开始刷新系统配置缓存");
            
            // 清空本地缓存
            localCache.clear();
            
            // 重新加载所有配置
            List<SysConfig> configs = sysConfigMapper.selectAllConfigs();
            for (SysConfig config : configs) {
                localCache.put(config.getConfigKey(), config.getConfigValue());
            }
            
            cacheInitialized = true;
            log.info("系统配置缓存刷新完成，共加载 {} 个配置项", localCache.size());
        } catch (Exception e) {
            log.error("刷新系统配置缓存失败", e);
            cacheInitialized = false;
        }
    }

    @Override
    @Transactional
    public int batchSetConfigs(Map<String, String> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            if (setConfigValue(entry.getKey(), entry.getValue())) {
                successCount++;
            }
        }

        // 批量操作后刷新缓存
        if (successCount > 0) {
            refreshCache();
        }

        return successCount;
    }

    @Override
    @Cacheable(value = "sysConfig", key = "#configKey + '_full'")
    public SysConfig getConfig(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }

        try {
            return sysConfigMapper.selectByKey(configKey);
        } catch (Exception e) {
            log.error("获取配置信息失败: configKey={}", configKey, e);
            return null;
        }
    }
}
