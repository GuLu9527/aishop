package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.dto.SupplierDTO;
import com.supermarket.dto.SupplierQueryDTO;
import com.supermarket.entity.Supplier;
import com.supermarket.mapper.SupplierMapper;
import com.supermarket.service.SupplierService;
import com.supermarket.vo.SupplierVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 供货商服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    private final SupplierMapper supplierMapper;

    @Override
    public IPage<SupplierVO> getSupplierPage(SupplierQueryDTO query) {
        Page<SupplierVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return supplierMapper.selectSupplierPage(page, query);
    }

    @Override
    public SupplierVO getSupplierById(Long id) {
        return supplierMapper.selectSupplierById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSupplier(SupplierDTO supplierDTO) {
        // 检查供货商名称是否已存在
        if (existsByName(supplierDTO.getSupplierName(), null)) {
            throw new RuntimeException("供货商名称已存在");
        }

        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO, supplier);
        
        // 设置默认值
        if (supplier.getCreditRating() == null) {
            supplier.setCreditRating(5); // 默认5星
        }
        if (supplier.getStatus() == null) {
            supplier.setStatus(1); // 默认启用
        }

        return this.save(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSupplier(SupplierDTO supplierDTO) {
        if (supplierDTO.getId() == null) {
            throw new RuntimeException("供货商ID不能为空");
        }

        // 检查供货商是否存在
        Supplier existingSupplier = this.getById(supplierDTO.getId());
        if (existingSupplier == null) {
            throw new RuntimeException("供货商不存在");
        }

        // 检查供货商名称是否已存在（排除自己）
        if (existsByName(supplierDTO.getSupplierName(), supplierDTO.getId())) {
            throw new RuntimeException("供货商名称已存在");
        }

        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO, supplier);

        return this.updateById(supplier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSupplier(Long id) {
        // 检查供货商是否存在
        Supplier supplier = this.getById(id);
        if (supplier == null) {
            throw new RuntimeException("供货商不存在");
        }

        // TODO: 检查是否有关联的采购订单或商品，如果有则不允许删除
        // 这里可以添加业务逻辑检查

        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSuppliers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("供货商ID列表不能为空");
        }

        // TODO: 批量检查是否有关联数据
        
        return this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 检查供货商是否存在
        Supplier supplier = this.getById(id);
        if (supplier == null) {
            throw new RuntimeException("供货商不存在");
        }

        supplier.setStatus(status);
        return this.updateById(supplier);
    }

    @Override
    public List<SupplierVO> getActiveSuppliers() {
        return supplierMapper.selectActiveSuppliers();
    }

    @Override
    public boolean existsByName(String supplierName, Long excludeId) {
        if (!StringUtils.hasText(supplierName)) {
            return false;
        }
        return supplierMapper.countByName(supplierName, excludeId) > 0;
    }

    @Override
    public java.util.Map<String, Object> getSupplierStats() {
        return supplierMapper.getSupplierStats();
    }

    @Override
    public List<SupplierVO> getExportData(SupplierQueryDTO query) {
        // 设置大的页面大小以获取所有数据
        query.setPageSize(10000);
        query.setPageNum(1);

        // 获取分页数据
        IPage<SupplierVO> page = getSupplierPage(query);
        return page.getRecords();
    }
}
