package com.supermarket.service;

import com.supermarket.dto.AiCustomerChatRequest;
import com.supermarket.dto.AiServiceEvaluationRequest;
import com.supermarket.dto.AiServiceTicketRequest;
import com.supermarket.entity.AiCustomerSession;
import com.supermarket.entity.AiKnowledgeBase;
import com.supermarket.entity.AiServiceEvaluation;
import com.supermarket.entity.AiServiceTicket;
import com.supermarket.vo.AiCustomerChatResponse;
import com.supermarket.vo.AiCustomerSessionVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI客服服务接口
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
public interface AiCustomerService {

    /**
     * 处理客户消息（流式响应）
     */
    Flux<String> processCustomerMessage(AiCustomerChatRequest request);

    /**
     * 处理客户消息（普通响应）
     */
    AiCustomerChatResponse processCustomerMessageSync(AiCustomerChatRequest request);

    /**
     * 获取或创建客户会话
     */
    AiCustomerSession getOrCreateSession(Long customerId, String customerName, String customerContact);

    /**
     * 获取客户会话历史
     */
    List<AiCustomerSessionVO> getCustomerSessionHistory(Long customerId);

    /**
     * 获取会话详情
     */
    AiCustomerSessionVO getSessionDetail(String sessionId);

    /**
     * 结束会话
     */
    void endSession(String sessionId);

    /**
     * 转接人工客服
     */
    void transferToHuman(String sessionId, Long assignedTo);

    /**
     * 搜索知识库
     */
    List<AiKnowledgeBase> searchKnowledgeBase(String keyword);

    /**
     * 根据意图获取知识库
     */
    List<AiKnowledgeBase> getKnowledgeByIntent(String intent);

    /**
     * 提交服务评价
     */
    void submitEvaluation(AiServiceEvaluationRequest request);

    /**
     * 创建服务工单
     */
    AiServiceTicket createServiceTicket(AiServiceTicketRequest request);

    /**
     * 获取客户工单列表
     */
    List<AiServiceTicket> getCustomerTickets(Long customerId);

    /**
     * 获取需要人工介入的会话
     */
    List<AiCustomerSessionVO> getSessionsNeedingHumanIntervention();

    /**
     * 获取会话消息列表
     */
    List<Object> getSessionMessages(String sessionId, int page, int size);
}
