package com.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.SupplierDTO;
import com.supermarket.dto.SupplierQueryDTO;
import com.supermarket.entity.Supplier;
import com.supermarket.vo.SupplierVO;

import java.util.List;

/**
 * 供货商服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface SupplierService extends IService<Supplier> {

    /**
     * 分页查询供货商列表
     *
     * @param query 查询条件
     * @return 供货商分页列表
     */
    IPage<SupplierVO> getSupplierPage(SupplierQueryDTO query);

    /**
     * 根据ID查询供货商详情
     *
     * @param id 供货商ID
     * @return 供货商详情
     */
    SupplierVO getSupplierById(Long id);

    /**
     * 添加供货商
     *
     * @param supplierDTO 供货商信息
     * @return 是否成功
     */
    boolean addSupplier(SupplierDTO supplierDTO);

    /**
     * 更新供货商
     *
     * @param supplierDTO 供货商信息
     * @return 是否成功
     */
    boolean updateSupplier(SupplierDTO supplierDTO);

    /**
     * 删除供货商
     *
     * @param id 供货商ID
     * @return 是否成功
     */
    boolean deleteSupplier(Long id);

    /**
     * 批量删除供货商
     *
     * @param ids 供货商ID列表
     * @return 是否成功
     */
    boolean deleteSuppliers(List<Long> ids);

    /**
     * 启用/禁用供货商
     *
     * @param id     供货商ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 查询所有启用的供货商
     *
     * @return 启用的供货商列表
     */
    List<SupplierVO> getActiveSuppliers();

    /**
     * 检查供货商名称是否存在
     *
     * @param supplierName 供货商名称
     * @param excludeId    排除的ID（用于更新时排除自己）
     * @return 是否存在
     */
    boolean existsByName(String supplierName, Long excludeId);

    /**
     * 获取供货商统计信息
     *
     * @return 统计信息
     */
    java.util.Map<String, Object> getSupplierStats();

    /**
     * 获取供货商导出数据
     *
     * @param query 查询条件
     * @return 供货商列表
     */
    List<SupplierVO> getExportData(SupplierQueryDTO query);
}
