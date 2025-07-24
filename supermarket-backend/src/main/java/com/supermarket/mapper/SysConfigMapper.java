package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统配置Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{configKey}")
    String selectValueByKey(@Param("configKey") String configKey);

    /**
     * 根据配置键查询配置信息
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    @Select("SELECT * FROM sys_config WHERE config_key = #{configKey}")
    SysConfig selectByKey(@Param("configKey") String configKey);

    /**
     * 更新配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 影响行数
     */
    @Update("UPDATE sys_config SET config_value = #{configValue}, update_time = NOW() WHERE config_key = #{configKey}")
    int updateValueByKey(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 查询所有配置
     *
     * @return 配置列表
     */
    @Select("SELECT * FROM sys_config ORDER BY id")
    List<SysConfig> selectAllConfigs();

    /**
     * 根据配置键前缀查询配置列表
     *
     * @param keyPrefix 配置键前缀
     * @return 配置列表
     */
    @Select("SELECT * FROM sys_config WHERE config_key LIKE CONCAT(#{keyPrefix}, '%') ORDER BY config_key")
    List<SysConfig> selectByKeyPrefix(@Param("keyPrefix") String keyPrefix);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 存在数量
     */
    @Select("SELECT COUNT(*) FROM sys_config WHERE config_key = #{configKey}")
    int countByKey(@Param("configKey") String configKey);
}
