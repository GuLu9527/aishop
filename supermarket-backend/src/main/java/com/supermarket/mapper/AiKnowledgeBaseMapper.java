package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.AiKnowledgeBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI知识库Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface AiKnowledgeBaseMapper extends BaseMapper<AiKnowledgeBase> {

    /**
     * 根据关键词搜索知识库
     */
    List<AiKnowledgeBase> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据意图查询知识库
     */
    List<AiKnowledgeBase> findByIntent(@Param("intent") String intent);

    /**
     * 根据分类查询知识库
     */
    List<AiKnowledgeBase> findByCategory(@Param("category") String category);

    /**
     * 增加命中次数
     */
    int incrementHitCount(@Param("id") Long id);

    /**
     * 获取热门知识点
     */
    List<AiKnowledgeBase> findPopularKnowledge(@Param("limit") Integer limit);
}