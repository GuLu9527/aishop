package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.supermarket.dto.AiCustomerChatRequest;
import com.supermarket.dto.AiServiceEvaluationRequest;
import com.supermarket.dto.AiServiceTicketRequest;
import com.supermarket.entity.*;
import com.supermarket.mapper.*;
import com.supermarket.service.AiCustomerService;
import com.supermarket.vo.AiCustomerChatResponse;
import com.supermarket.vo.AiCustomerSessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI客服服务实现类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiCustomerServiceImpl implements AiCustomerService {

    private final AiCustomerSessionMapper sessionMapper;
    private final AiKnowledgeBaseMapper knowledgeBaseMapper;
    private final AiServiceEvaluationMapper evaluationMapper;
    private final AiServiceTicketMapper ticketMapper;
    private final AiMessageMapper messageMapper;
    private final TongyiAiServiceImpl tongyiAiService;

    @Override
    public Flux<String> processCustomerMessage(AiCustomerChatRequest request) {
        return Flux.create(sink -> {
            try {
                // 获取或创建会话
                AiCustomerSession session = getOrCreateSession(
                    request.getCustomerId(), 
                    request.getCustomerName(), 
                    request.getCustomerContact()
                );

                // 保存用户消息
                AiMessage userMessage = saveUserMessage(session, request);

                // 搜索知识库
                List<AiKnowledgeBase> knowledgeList = searchKnowledgeBase(request.getMessage());
                
                // 构建AI提示词
                String prompt = buildPrompt(request.getMessage(), knowledgeList);
                
                // 调用AI服务
                long startTime = System.currentTimeMillis();
                String aiResponse = tongyiAiService.chat(prompt);
                long processingTime = System.currentTimeMillis() - startTime;

                // 分析是否需要人工介入
                boolean needHumanIntervention = analyzeNeedHumanIntervention(request.getMessage(), aiResponse);

                // 保存AI回复
                AiMessage aiMessage = saveAiMessage(session, aiResponse, processingTime, needHumanIntervention, knowledgeList);

                // 更新会话信息
                updateSessionInfo(session, aiResponse, needHumanIntervention);

                // 流式返回响应
                String[] words = aiResponse.split("\\s+");
                for (String word : words) {
                    sink.next(word + " ");
                    try {
                        Thread.sleep(50); // 模拟流式输出
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                sink.complete();

            } catch (Exception e) {
                log.error("处理客户消息失败", e);
                sink.error(e);
            }
        });
    }

    @Override
    @Transactional
    public AiCustomerChatResponse processCustomerMessageSync(AiCustomerChatRequest request) {
        try {
            // 获取或创建会话
            AiCustomerSession session = getOrCreateSession(
                request.getCustomerId(), 
                request.getCustomerName(), 
                request.getCustomerContact()
            );

            // 保存用户消息
            AiMessage userMessage = saveUserMessage(session, request);

            // 搜索知识库
            List<AiKnowledgeBase> knowledgeList = searchKnowledgeBase(request.getMessage());
            
            // 构建AI提示词
            String prompt = buildPrompt(request.getMessage(), knowledgeList);
            
            // 调用AI服务
            long startTime = System.currentTimeMillis();
            String aiResponse = tongyiAiService.chat(prompt);
            long processingTime = System.currentTimeMillis() - startTime;

            // 分析意图
            String intent = analyzeIntent(request.getMessage());

            // 分析是否需要人工介入
            boolean needHumanIntervention = analyzeNeedHumanIntervention(request.getMessage(), aiResponse);

            // 保存AI回复
            AiMessage aiMessage = saveAiMessage(session, aiResponse, processingTime, needHumanIntervention, knowledgeList);

            // 更新会话信息
            updateSessionInfo(session, aiResponse, needHumanIntervention);

            // 构建响应
            AiCustomerChatResponse response = new AiCustomerChatResponse();
            response.setSessionId(session.getSessionId());
            response.setMessageId(aiMessage.getId());
            response.setContent(aiResponse);
            response.setIntent(intent);
            response.setNeedHumanIntervention(needHumanIntervention);
            response.setKnowledgeBaseId(knowledgeList.isEmpty() ? null : knowledgeList.get(0).getId());
            response.setProcessingTime(processingTime);
            response.setSuccess(true);
            response.setCreateTime(LocalDateTime.now());

            return response;

        } catch (Exception e) {
            log.error("处理客户消息失败", e);
            
            AiCustomerChatResponse response = new AiCustomerChatResponse();
            response.setSuccess(false);
            response.setErrorMessage("处理消息失败：" + e.getMessage());
            response.setCreateTime(LocalDateTime.now());
            
            return response;
        }
    }

    @Override
    @Transactional
    public AiCustomerSession getOrCreateSession(Long customerId, String customerName, String customerContact) {
        // 查找活跃会话
        AiCustomerSession activeSession = sessionMapper.findActiveSessionByCustomerId(customerId);
        
        if (activeSession != null) {
            return activeSession;
        }

        // 创建新会话
        AiCustomerSession newSession = new AiCustomerSession();
        newSession.setSessionId(UUID.randomUUID().toString());
        newSession.setCustomerId(customerId);
        newSession.setCustomerName(customerName);
        newSession.setCustomerContact(customerContact);
        newSession.setSessionTitle("客服咨询 - " + LocalDateTime.now().toString().substring(0, 16));
        newSession.setStatus(1); // 1-进行中
        newSession.setMessageCount(0);
        newSession.setNeedHumanIntervention(false);
        newSession.setCreateTime(LocalDateTime.now());
        newSession.setUpdateTime(LocalDateTime.now());

        sessionMapper.insert(newSession);
        return newSession;
    }

    @Override
    public List<AiCustomerSessionVO> getCustomerSessionHistory(Long customerId) {
        List<AiCustomerSession> sessions = sessionMapper.findHistoryByCustomerId(customerId);
        return sessions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public AiCustomerSessionVO getSessionDetail(String sessionId) {
        AiCustomerSession session = sessionMapper.findBySessionId(sessionId);
        return session != null ? convertToVO(session) : null;
    }

    @Override
    @Transactional
    public void endSession(String sessionId) {
        AiCustomerSession session = sessionMapper.findBySessionId(sessionId);
        if (session != null) {
            session.setStatus(2); // 2-已结束
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }

    @Override
    @Transactional
    public void transferToHuman(String sessionId, Long assignedTo) {
        AiCustomerSession session = sessionMapper.findBySessionId(sessionId);
        if (session != null) {
            session.setStatus(3); // 3-转人工
            session.setAssignedTo(assignedTo);
            session.setNeedHumanIntervention(true);
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }

    @Override
    public List<AiKnowledgeBase> searchKnowledgeBase(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        
        List<AiKnowledgeBase> results = knowledgeBaseMapper.searchByKeyword(keyword);
        
        // 增加命中次数
        results.forEach(kb -> knowledgeBaseMapper.incrementHitCount(kb.getId()));
        
        return results;
    }

    @Override
    public List<AiKnowledgeBase> getKnowledgeByIntent(String intent) {
        return knowledgeBaseMapper.findByIntent(intent);
    }

    @Override
    @Transactional
    public void submitEvaluation(AiServiceEvaluationRequest request) {
        AiServiceEvaluation evaluation = new AiServiceEvaluation();
        BeanUtils.copyProperties(request, evaluation);
        evaluation.setCreateTime(LocalDateTime.now());
        evaluationMapper.insert(evaluation);

        // 更新会话满意度评分
        AiCustomerSession session = sessionMapper.findBySessionId(request.getSessionId());
        if (session != null) {
            session.setSatisfactionScore(BigDecimal.valueOf(request.getSatisfactionScore()));
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }

    @Override
    @Transactional
    public AiServiceTicket createServiceTicket(AiServiceTicketRequest request) {
        AiServiceTicket ticket = new AiServiceTicket();
        BeanUtils.copyProperties(request, ticket);
        ticket.setTicketNo(generateTicketNo());
        ticket.setStatus(String.valueOf(1)); // 1-开放
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        
        ticketMapper.insert(ticket);
        return ticket;
    }

    @Override
    public List<AiServiceTicket> getCustomerTickets(Long customerId) {
        return ticketMapper.findByCustomerId(customerId);
    }

    @Override
    public List<AiCustomerSessionVO> getSessionsNeedingHumanIntervention() {
        List<AiCustomerSession> sessions = sessionMapper.findSessionsNeedingHumanIntervention();
        return sessions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<Object> getSessionMessages(String sessionId, int page, int size) {
        // 分页查询会话消息
        QueryWrapper<AiMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("session_id", sessionId)
                   .orderByAsc("create_time");
        
        List<AiMessage> messages = messageMapper.selectList(queryWrapper);
        
        // 转换为前端需要的格式
        return messages.stream().map(msg -> {
            MessageVO vo = new MessageVO();
            vo.setId(msg.getId());
            vo.setContent(msg.getContent());
            vo.setIsUser(msg.getMessageType() == 1);
            vo.setIntent(msg.getIntent());
            vo.setNeedHumanIntervention(msg.getNeedHumanIntervention());
            vo.setCreateTime(msg.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    // 消息VO类
    public static class MessageVO {
        private Long id;
        private String content;
        private Boolean isUser;
        private String intent;
        private Boolean needHumanIntervention;
        private LocalDateTime createTime;
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Boolean getIsUser() { return isUser; }
        public void setIsUser(Boolean isUser) { this.isUser = isUser; }
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        public Boolean getNeedHumanIntervention() { return needHumanIntervention; }
        public void setNeedHumanIntervention(Boolean needHumanIntervention) { this.needHumanIntervention = needHumanIntervention; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    }

    // 私有方法

    private AiMessage saveUserMessage(AiCustomerSession session, AiCustomerChatRequest request) {
        AiMessage message = new AiMessage();
        message.setConversationId(session.getId()); // 设置会话ID
        message.setSessionId(session.getSessionId());
        message.setMessageType(1); // 用户消息
        message.setContent(request.getMessage());
        message.setUserId(request.getCustomerId());
        message.setUserName(request.getCustomerName());
        message.setSource("ai_customer_service");
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        return message;
    }

    private AiMessage saveAiMessage(AiCustomerSession session, String content, long processingTime, 
                                   boolean needHumanIntervention, List<AiKnowledgeBase> knowledgeList) {
        AiMessage message = new AiMessage();
        message.setConversationId(session.getId()); // 设置会话ID
        message.setSessionId(session.getSessionId());
        message.setMessageType(2); // AI回复
        message.setContent(content);
        message.setProcessingTime(processingTime);
        message.setSuccess(true);
        message.setSource("ai_customer_service");
        message.setNeedHumanIntervention(needHumanIntervention);
        message.setKnowledgeBaseId(knowledgeList.isEmpty() ? null : knowledgeList.get(0).getId());
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        return message;
    }

    private void updateSessionInfo(AiCustomerSession session, String lastMessage, boolean needHumanIntervention) {
        session.setMessageCount(session.getMessageCount() + 2); // 用户消息 + AI回复
        session.setLastMessageContent(lastMessage);
        session.setLastMessageTime(LocalDateTime.now());
        session.setNeedHumanIntervention(needHumanIntervention);
        session.setUpdateTime(LocalDateTime.now());
        
        sessionMapper.updateById(session);
    }

    private String buildPrompt(String userMessage, List<AiKnowledgeBase> knowledgeList) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的超市客服助手，请根据以下知识库信息回答客户问题：\n\n");
        
        if (!knowledgeList.isEmpty()) {
            prompt.append("相关知识库信息：\n");
            for (AiKnowledgeBase kb : knowledgeList) {
                prompt.append("- ").append(kb.getTitle()).append("：").append(kb.getContent()).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("客户问题：").append(userMessage).append("\n\n");
        prompt.append("请提供专业、友好、准确的回答。如果问题超出知识库范围，请建议客户联系人工客服。");
        
        return prompt.toString();
    }

    private String analyzeIntent(String message) {
        // 简单的意图分析逻辑
        if (message.contains("退货") || message.contains("退款")) {
            return "refund";
        } else if (message.contains("订单") || message.contains("购买")) {
            return "order";
        } else if (message.contains("商品") || message.contains("产品")) {
            return "product";
        } else if (message.contains("投诉") || message.contains("问题")) {
            return "complaint";
        } else {
            return "general";
        }
    }

    private boolean analyzeNeedHumanIntervention(String userMessage, String aiResponse) {
        // 分析是否需要人工介入的逻辑
        return userMessage.contains("投诉") || 
               userMessage.contains("退款") || 
               userMessage.contains("人工客服") ||
               aiResponse.contains("联系人工客服");
    }

    private String generateTicketNo() {
        return "TK" + System.currentTimeMillis();
    }

    private AiCustomerSessionVO convertToVO(AiCustomerSession session) {
        AiCustomerSessionVO vo = new AiCustomerSessionVO();
        BeanUtils.copyProperties(session, vo);
        return vo;
    }
}