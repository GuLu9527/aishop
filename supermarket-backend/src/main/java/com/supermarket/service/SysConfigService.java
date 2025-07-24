package com.supermarket.service;

import com.supermarket.entity.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface SysConfigService {

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值，如果不存在返回null
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值，如果不存在返回默认值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置键获取整数配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 整数配置值
     */
    Integer getIntValue(String configKey, Integer defaultValue);

    /**
     * 根据配置键获取布尔配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 布尔配置值
     */
    Boolean getBooleanValue(String configKey, Boolean defaultValue);

    /**
     * 根据配置键获取长整型配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 长整型配置值
     */
    Long getLongValue(String configKey, Long defaultValue);

    /**
     * 根据配置键获取双精度配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 双精度配置值
     */
    Double getDoubleValue(String configKey, Double defaultValue);

    /**
     * 设置配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     */
    boolean setConfigValue(String configKey, String configValue);

    /**
     * 设置配置值（带描述）
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @param description 配置描述
     * @return 是否成功
     */
    boolean setConfigValue(String configKey, String configValue, String description);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     * @return 是否成功
     */
    boolean deleteConfig(String configKey);

    /**
     * 获取所有配置
     *
     * @return 配置列表
     */
    List<SysConfig> getAllConfigs();

    /**
     * 根据配置键前缀获取配置列表
     *
     * @param keyPrefix 配置键前缀
     * @return 配置列表
     */
    List<SysConfig> getConfigsByPrefix(String keyPrefix);

    /**
     * 获取配置映射（键值对）
     *
     * @return 配置映射
     */
    Map<String, String> getConfigMap();

    /**
     * 根据配置键前缀获取配置映射
     *
     * @param keyPrefix 配置键前缀
     * @return 配置映射
     */
    Map<String, String> getConfigMapByPrefix(String keyPrefix);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean existsConfig(String configKey);

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    /**
     * 批量设置配置
     *
     * @param configMap 配置映射
     * @return 成功设置的数量
     */
    int batchSetConfigs(Map<String, String> configMap);

    /**
     * 获取配置信息（包含描述等完整信息）
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    SysConfig getConfig(String configKey);
}
