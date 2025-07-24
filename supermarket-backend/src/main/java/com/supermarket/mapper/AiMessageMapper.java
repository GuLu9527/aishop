package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * AI对话消息Mapper接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessage> {

    /**
     * 根据会话ID查询消息历史
     *
     * @param conversationId 会话ID
     * @param limit          限制数量
     * @return 消息列表
     */
    @Select("SELECT * FROM ai_message WHERE conversation_id = #{conversationId} ORDER BY create_time ASC LIMIT #{limit}")
    List<AiMessage> selectByConversationId(@Param("conversationId") String conversationId, @Param("limit") Integer limit);

    /**
     * 根据会话SessionId查询消息历史
     *
     * @param sessionId 会话SessionId
     * @param limit     限制数量
     * @return 消息列表
     */
    @Select("SELECT * FROM ai_message WHERE session_id = #{sessionId} ORDER BY create_time ASC LIMIT #{limit}")
    List<AiMessage> selectBySessionId(@Param("sessionId") String sessionId, @Param("limit") Integer limit);

    /**
     * 根据会话SessionId删除消息
     *
     * @param sessionId 会话SessionId
     * @return 删除数量
     */
    @Select("DELETE FROM ai_message WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID查询最新消息
     *
     * @param conversationId 会话ID
     * @param limit          限制数量
     * @return 消息列表
     */
    @Select("SELECT * FROM ai_message WHERE conversation_id = #{conversationId} ORDER BY create_time DESC LIMIT #{limit}")
    List<AiMessage> selectLatestByConversationId(@Param("conversationId") String conversationId, @Param("limit") Integer limit);

    /**
     * 查询会话消息统计
     *
     * @param conversationId 会话ID
     * @return 统计信息
     */
    @Select("SELECT COUNT(*) as total_count, " +
            "SUM(CASE WHEN role = 'USER' THEN 1 ELSE 0 END) as user_count, " +
            "SUM(CASE WHEN role = 'ASSISTANT' THEN 1 ELSE 0 END) as assistant_count " +
            "FROM ai_message WHERE conversation_id = #{conversationId}")
    Map<String, Object> selectMessageStats(@Param("conversationId") String conversationId);

    /**
     * 删除会话的所有消息
     *
     * @param conversationId 会话ID
     * @return 删除数量
     */
    @Select("DELETE FROM ai_message WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);
}