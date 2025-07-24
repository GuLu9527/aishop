package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI对话会话Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversation> {

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} ORDER BY update_time DESC LIMIT #{limit}")
    List<AiConversation> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据用户ID查询会话列表（无分页）
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} ORDER BY update_time DESC")
    List<AiConversation> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID查询会话
     *
     * @param sessionId 会话ID
     * @return 会话信息
     */
    @Select("SELECT * FROM ai_conversation WHERE session_id = #{sessionId}")
    AiConversation selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话标识查询会话
     *
     * @param conversationKey 会话标识
     * @return 会话信息
     */
    @Select("SELECT * FROM ai_conversation WHERE conversation_key = #{conversationKey}")
    AiConversation selectByConversationKey(@Param("conversationKey") String conversationKey);

    /**
     * 查询用户活跃会话
     *
     * @param userId 用户ID
     * @return 活跃会话列表
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND status = 'ACTIVE' ORDER BY update_time DESC")
    List<AiConversation> selectActiveByUserId(@Param("userId") Long userId);
}