package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.dto.SupplierQueryDTO;
import com.supermarket.entity.Supplier;
import com.supermarket.vo.SupplierVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供货商Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {

    /**
     * 分页查询供货商列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 供货商分页列表
     */
    IPage<SupplierVO> selectSupplierPage(Page<SupplierVO> page, @Param("query") SupplierQueryDTO query);

    /**
     * 根据ID查询供货商详情
     *
     * @param id 供货商ID
     * @return 供货商详情
     */
    SupplierVO selectSupplierById(@Param("id") Long id);

    /**
     * 查询所有启用的供货商
     *
     * @return 启用的供货商列表
     */
    List<SupplierVO> selectActiveSuppliers();

    /**
     * 根据名称查询供货商（用于重名检查）
     *
     * @param supplierName 供货商名称
     * @param excludeId    排除的ID（用于更新时排除自己）
     * @return 供货商数量
     */
    int countByName(@Param("supplierName") String supplierName, @Param("excludeId") Long excludeId);

    /**
     * 获取供货商统计信息
     *
     * @return 统计信息
     */
    java.util.Map<String, Object> getSupplierStats();
}
