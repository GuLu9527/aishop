package com.supermarket.controller;

import com.supermarket.dto.AiCustomerChatRequest;
import com.supermarket.dto.AiServiceEvaluationRequest;
import com.supermarket.dto.AiServiceTicketRequest;
import com.supermarket.entity.AiCustomerSession;
import com.supermarket.entity.AiKnowledgeBase;
import com.supermarket.entity.AiServiceTicket;
import com.supermarket.service.AiCustomerService;
import com.supermarket.vo.AiCustomerChatResponse;
import com.supermarket.vo.AiCustomerSessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;
import java.util.List;

/**
 * AI客服控制器
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/ai/customer")
@RequiredArgsConstructor
@Validated
@Tag(name = "AI客服", description = "AI客服相关接口")
@CrossOrigin(origins = "*")
public class AiCustomerController {

    private final AiCustomerService aiCustomerService;


    @Operation(summary = "发送消息（流式响应）", description = "向AI客服发送消息，返回流式响应")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chatStream(@Valid @RequestBody AiCustomerChatRequest request) {
        log.info("收到客户流式聊天请求: customerId={}, message={}", request.getCustomerId(), request.getMessage());
        return aiCustomerService.processCustomerMessage(request);
    }

    @Operation(summary = "发送消息（同步响应）", description = "向AI客服发送消息，返回同步响应")
    @PostMapping("/chat")
    public ResponseEntity<AiCustomerChatResponse> chat(@Valid @RequestBody AiCustomerChatRequest request) {
        log.info("收到客户聊天请求: customerId={}, message={}", request.getCustomerId(), request.getMessage());
        AiCustomerChatResponse response = aiCustomerService.processCustomerMessageSync(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "获取或创建客户会话", description = "获取或创建客户会话")
    @GetMapping("/session")
    public ResponseEntity<AiCustomerSessionVO> getOrCreateSession(
            @Parameter(description = "客户ID") @RequestParam Long customerId,
            @Parameter(description = "客户姓名") @RequestParam String customerName,
            @Parameter(description = "客户联系方式") @RequestParam(required = false) String customerContact) {
        log.info("获取或创建客户会话: customerId={}, customerName={}", customerId, customerName);
        AiCustomerSession session = aiCustomerService.getOrCreateSession(customerId, customerName, customerContact);
        AiCustomerSessionVO sessionVO = convertToSessionVO(session);
        return ResponseEntity.ok(sessionVO);
    }

    @Operation(summary = "获取会话历史消息", description = "获取指定会话的历史消息")
    @GetMapping("/session/{sessionId}/history")
    public ResponseEntity<List<Object>> getSessionHistory(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        log.info("获取会话历史消息: sessionId={}, page={}, size={}", sessionId, page, size);
        List<Object> messages = aiCustomerService.getSessionMessages(sessionId, page, size);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "获取客户会话历史", description = "获取指定客户的会话历史记录")
    @GetMapping("/sessions/{customerId}")
    public ResponseEntity<List<AiCustomerSessionVO>> getSessionHistory(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        log.info("获取客户会话历史: customerId={}", customerId);
        List<AiCustomerSessionVO> sessions = aiCustomerService.getCustomerSessionHistory(customerId);
        return ResponseEntity.ok(sessions);
    }

    @Operation(summary = "获取会话详情", description = "获取指定会话的详细信息")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<AiCustomerSessionVO> getSessionDetail(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("获取会话详情: sessionId={}", sessionId);
        AiCustomerSessionVO session = aiCustomerService.getSessionDetail(sessionId);
        return ResponseEntity.ok(session);
    }

    @Operation(summary = "结束会话", description = "结束指定的客服会话")
    @PostMapping("/session/{sessionId}/end")
    public ResponseEntity<Void> endSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("结束会话: sessionId={}", sessionId);
        aiCustomerService.endSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "转接人工客服", description = "将会话转接给人工客服")
    @PostMapping("/session/{sessionId}/transfer")
    public ResponseEntity<Void> transferToHuman(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @RequestBody(required = false) TransferRequest request) {
        log.info("转接人工客服: sessionId={}, reason={}", sessionId, request != null ? request.getReason() : "");
        aiCustomerService.transferToHuman(sessionId, request != null ? request.getAssignedTo() : null);
        return ResponseEntity.ok().build();
    }

    // 转接请求DTO
    public static class TransferRequest {
        private Long assignedTo;
        private String reason;
        
        public Long getAssignedTo() { return assignedTo; }
        public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    @Operation(summary = "搜索知识库", description = "根据关键词搜索知识库")
    @GetMapping("/knowledge/search")
    public ResponseEntity<List<AiKnowledgeBase>> searchKnowledge(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        log.info("搜索知识库: keyword={}", keyword);
        List<AiKnowledgeBase> results = aiCustomerService.searchKnowledgeBase(keyword);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "根据意图获取知识库", description = "根据意图获取相关知识库内容")
    @GetMapping("/knowledge/intent/{intent}")
    public ResponseEntity<List<AiKnowledgeBase>> getKnowledgeByIntent(
            @Parameter(description = "意图") @PathVariable String intent) {
        log.info("根据意图获取知识库: intent={}", intent);
        List<AiKnowledgeBase> results = aiCustomerService.getKnowledgeByIntent(intent);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "提交服务评价", description = "客户提交对AI客服的服务评价")
    @PostMapping("/evaluation")
    public ResponseEntity<Void> submitEvaluation(@Valid @RequestBody AiServiceEvaluationRequest request) {
        log.info("提交服务评价: sessionId={}, satisfactionScore={}", 
                request.getSessionId(), request.getSatisfactionScore());
        aiCustomerService.submitEvaluation(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "创建服务工单", description = "创建客服工单")
    @PostMapping("/ticket")
    public ResponseEntity<AiServiceTicket> createTicket(@Valid @RequestBody AiServiceTicketRequest request) {
        log.info("创建服务工单: customerId={}, issueType={}", request.getCustomerId(), request.getIssueType());
        AiServiceTicket ticket = aiCustomerService.createServiceTicket(request);
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "获取客户工单", description = "获取指定客户的工单列表")
    @GetMapping("/tickets/{customerId}")
    public ResponseEntity<List<AiServiceTicket>> getCustomerTickets(
            @Parameter(description = "客户ID") @PathVariable Long customerId) {
        log.info("获取客户工单: customerId={}", customerId);
        List<AiServiceTicket> tickets = aiCustomerService.getCustomerTickets(customerId);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "获取需要人工介入的会话", description = "获取所有需要人工介入的会话列表")
    @GetMapping("/sessions/human-intervention")
    public ResponseEntity<List<AiCustomerSessionVO>> getSessionsNeedingHumanIntervention() {
        log.info("获取需要人工介入的会话");
        List<AiCustomerSessionVO> sessions = aiCustomerService.getSessionsNeedingHumanIntervention();
        return ResponseEntity.ok(sessions);
    }

    // 辅助方法：转换会话实体为VO
    private AiCustomerSessionVO convertToSessionVO(AiCustomerSession session) {
        AiCustomerSessionVO vo = new AiCustomerSessionVO();
        vo.setSessionId(session.getSessionId());
        vo.setCustomerId(session.getCustomerId());
        vo.setCustomerName(session.getCustomerName());
        vo.setCustomerPhone(session.getCustomerPhone());
        vo.setSessionTitle(session.getSessionTitle());
        vo.setStatus(session.getStatus());
        vo.setMessageCount(session.getMessageCount());
        vo.setLastMessageContent(session.getLastMessageContent());
        vo.setLastMessageTime(session.getLastMessageTime());
        vo.setNeedHumanIntervention(session.getNeedHumanIntervention());
        vo.setAssignedStaffId(session.getAssignedStaffId());
        vo.setAssignedStaffName(session.getAssignedStaffName());
        vo.setCustomerSatisfaction(session.getCustomerSatisfaction());
        vo.setCreateTime(session.getCreateTime());
        vo.setUpdateTime(session.getUpdateTime());
        return vo;
    }
}
