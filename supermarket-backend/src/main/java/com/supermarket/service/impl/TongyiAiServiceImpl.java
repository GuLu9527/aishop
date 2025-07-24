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

    @Value("${spring.ai.alibaba.dashscope.api-key:sk-test-key}")
    private String apiKey;

    @Value("${spring.ai.alibaba.dashscope.chat.options.model:qwen-plus}")
    private String model;

    @Value("${spring.ai.alibaba.dashscope.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.alibaba.dashscope.chat.options.max-tokens:2000}")
    private Integer maxTokens;

    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    /**
     * 调用通义千问API进行对话
     */
    public String chat(String message) {
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
        return apiKey != null && !apiKey.equals("sk-test-key") && !apiKey.trim().isEmpty();
    }
}