package com.supermarket.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云通义千问AI服务实现
 * 
 * @author AI Assistant
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TongyiAiServiceImpl {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.alibaba.dashscope.api-key:}")
    private String apiKey;

    @Value("${spring.ai.alibaba.dashscope.chat.options.model:qwen-plus}")
    private String model;

    @Value("${spring.ai.alibaba.dashscope.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.alibaba.dashscope.chat.options.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${ai.mock.enabled:true}")
    private Boolean mockEnabled;

    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    /**
     * 调用通义千问API进行对话
     */
    public String chat(String message) {
        // 如果启用模拟模式或API Key未配置，返回模拟响应
        if (mockEnabled || !isConfigured()) {
            return getMockResponse(message);
        }
        
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", message);
            requestBody.put("input", input);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", temperature);
            parameters.put("max_tokens", maxTokens);
            requestBody.put("parameters", parameters);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                DASHSCOPE_API_URL,
                HttpMethod.POST,
                entity,
                String.class
            );

            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode output = jsonNode.path("output");
                if (output.has("text")) {
                    return output.get("text").asText();
                }
            }

            log.warn("AI服务响应异常: {}", response.getBody());
            return "抱歉，AI服务暂时不可用，请稍后再试。";

        } catch (Exception e) {
            log.error("调用AI服务失败", e);
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 检查API配置是否有效
     */
    public boolean isConfigured() {
        boolean configured = apiKey != null && !apiKey.trim().isEmpty() && !"your-api-key-here".equals(apiKey);
        log.debug("AI服务配置检查: apiKey存在={}, apiKey长度={}", 
                 configured, 
                 apiKey != null ? apiKey.length() : 0);
        return configured;
    }

    /**
     * 获取模拟响应
     */
    private String getMockResponse(String message) {
        log.info("使用模拟AI响应，用户消息: {}", message);
        
        // 根据用户消息内容返回不同的模拟响应
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("退货") || lowerMessage.contains("退款")) {
            return "关于退货退款，我们的政策是：商品在7天内可以无理由退货，需要保持商品完好。具体流程请联系客服办理。如需详细帮助，我可以为您转接人工客服。";
        } else if (lowerMessage.contains("订单") || lowerMessage.contains("查询")) {
            return "您可以通过订单号查询订单状态，或者在个人中心查看订单详情。如果遇到问题，请提供订单号，我来帮您查询。";
        } else if (lowerMessage.contains("商品") || lowerMessage.contains("产品")) {
            return "我们有丰富的商品种类，包括生鲜、日用品、电器等。您可以通过搜索或分类浏览找到需要的商品。有什么特定商品需要推荐吗？";
        } else if (lowerMessage.contains("优惠") || lowerMessage.contains("活动")) {
            return "我们经常有各种优惠活动，包括满减、折扣、买赠等。建议您关注我们的活动页面或会员中心获取最新优惠信息。";
        } else if (lowerMessage.contains("投诉") || lowerMessage.contains("问题")) {
            return "非常抱歉给您带来不便，我会认真记录您的问题。对于投诉类问题，建议转接人工客服为您详细处理。";
        } else {
            return "您好！我是AI客服助手，很高兴为您服务。我可以帮您解答关于商品、订单、退换货、优惠活动等问题。请详细描述您的需求，我会尽力为您解答。";
        }
    }
}
