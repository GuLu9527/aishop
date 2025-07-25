package com.supermarket.service;

import com.supermarket.dto.AiChatRequest;
import com.supermarket.dto.AiChatResponse;
import com.supermarket.service.impl.TongyiAiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI服务测试类
 * 用于测试通义千问连接和聊天功能
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class AiServiceTest {

    @Autowired
    private TongyiAiServiceImpl tongyiAiService;

    @Autowired
    private AiChatService aiChatService;

    @Test
    public void testTongyiAiServiceConfiguration() {
        log.info("=== 测试通义千问服务配置 ===");
        
        // 测试配置是否正确
        boolean isConfigured = tongyiAiService.isConfigured();
        log.info("AI服务配置状态: {}", isConfigured ? "已配置" : "未配置");
        
        assertTrue(isConfigured, "AI服务应该已经配置");
    }

    @Test
    public void testTongyiAiServiceConnection() {
        log.info("=== 测试通义千问服务连接 ===");
        
        // 先检查配置
        if (!tongyiAiService.isConfigured()) {
            log.warn("AI服务未配置，跳过连接测试");
            return;
        }

        try {
            // 测试简单对话
            String testMessage = "你好，请回复'连接测试成功'";
            String response = tongyiAiService.chat(testMessage);
            
            log.info("测试消息: {}", testMessage);
            log.info("AI响应: {}", response);
            
            assertNotNull(response, "AI响应不应该为空");
            assertFalse(response.trim().isEmpty(), "AI响应不应该为空字符串");
            assertFalse(response.contains("暂时不可用"), "AI服务应该正常工作");
            
        } catch (Exception e) {
            log.error("通义千问连接测试失败", e);
            fail("通义千问连接测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testTongyiAiServiceChat() {
        log.info("=== 测试通义千问聊天功能 ===");
        
        if (!tongyiAiService.isConfigured()) {
            log.warn("AI服务未配置，跳过聊天测试");
            return;
        }

        try {
            // 测试多轮对话
            String[] testMessages = {
                "你是什么AI助手？",
                "请介绍一下你的功能",
                "你能帮助超市管理吗？",
                "今天天气怎么样？"
            };

            for (String message : testMessages) {
                log.info("发送消息: {}", message);
                String response = tongyiAiService.chat(message);
                log.info("AI响应: {}", response);
                
                assertNotNull(response, "AI响应不应该为空");
                assertFalse(response.trim().isEmpty(), "AI响应不应该为空字符串");
                
                // 添加延迟避免请求过于频繁
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            log.error("通义千问聊天测试失败", e);
            fail("通义千问聊天测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testAiChatServiceIntegration() {
        log.info("=== 测试AI聊天服务集成 ===");
        
        try {
            // 创建测试请求
            AiChatRequest request = new AiChatRequest();
            request.setUserId(1L);
            request.setUserName("测试用户");
            request.setMessage("你好，我想了解今天的销售情况");
            
            log.info("发送聊天请求: {}", request.getMessage());
            
            // 调用聊天服务
            AiChatResponse response = aiChatService.chat(request);
            
            log.info("聊天服务响应: {}", response.getMessage());
            log.info("会话ID: {}", response.getSessionId());
            log.info("建议: {}", response.getSuggestions());
            
            // 验证响应
            assertNotNull(response, "聊天响应不应该为空");
            assertNotNull(response.getMessage(), "响应消息不应该为空");
            assertNotNull(response.getSessionId(), "会话ID不应该为空");
            assertFalse(response.getMessage().trim().isEmpty(), "响应消息不应该为空字符串");
            
        } catch (Exception e) {
            log.error("AI聊天服务集成测试失败", e);
            fail("AI聊天服务集成测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testSalesQueryScenario() {
        log.info("=== 测试销售查询场景 ===");
        
        try {
            // 测试销售相关查询
            String[] salesQueries = {
                "今天的销售情况怎么样？",
                "本周销售趋势如何？",
                "哪些商品卖得最好？",
                "今天的营业额是多少？"
            };

            for (String query : salesQueries) {
                AiChatRequest request = new AiChatRequest();
                request.setUserId(1L);
                request.setUserName("测试用户");
                request.setMessage(query);
                
                log.info("销售查询: {}", query);
                
                AiChatResponse response = aiChatService.chat(request);
                
                log.info("AI响应: {}", response.getMessage());
                
                assertNotNull(response, "响应不应该为空");
                assertNotNull(response.getMessage(), "响应消息不应该为空");
                
                // 添加延迟
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            log.error("销售查询场景测试失败", e);
            fail("销售查询场景测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testInventoryQueryScenario() {
        log.info("=== 测试库存查询场景 ===");
        
        try {
            // 测试库存相关查询
            String[] inventoryQueries = {
                "帮我查看库存情况",
                "哪些商品库存不足？",
                "需要补货的商品有哪些？",
                "库存最多的商品是什么？"
            };

            for (String query : inventoryQueries) {
                AiChatRequest request = new AiChatRequest();
                request.setUserId(1L);
                request.setUserName("测试用户");
                request.setMessage(query);
                
                log.info("库存查询: {}", query);
                
                AiChatResponse response = aiChatService.chat(request);
                
                log.info("AI响应: {}", response.getMessage());
                
                assertNotNull(response, "响应不应该为空");
                assertNotNull(response.getMessage(), "响应消息不应该为空");
                
                // 添加延迟
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            log.error("库存查询场景测试失败", e);
            fail("库存查询场景测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testErrorHandling() {
        log.info("=== 测试错误处理 ===");
        
        try {
            // 测试空消息
            AiChatRequest emptyRequest = new AiChatRequest();
            emptyRequest.setUserId(1L);
            emptyRequest.setUserName("测试用户");
            emptyRequest.setMessage("");
            
            try {
                AiChatResponse response = aiChatService.chat(emptyRequest);
                log.info("空消息响应: {}", response.getMessage());
            } catch (Exception e) {
                log.info("空消息正确抛出异常: {}", e.getMessage());
            }
            
            // 测试null消息
            AiChatRequest nullRequest = new AiChatRequest();
            nullRequest.setUserId(1L);
            nullRequest.setUserName("测试用户");
            nullRequest.setMessage(null);
            
            try {
                AiChatResponse response = aiChatService.chat(nullRequest);
                log.info("null消息响应: {}", response.getMessage());
            } catch (Exception e) {
                log.info("null消息正确抛出异常: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("错误处理测试失败", e);
            fail("错误处理测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testPerformance() {
        log.info("=== 测试性能 ===");
        
        if (!tongyiAiService.isConfigured()) {
            log.warn("AI服务未配置，跳过性能测试");
            return;
        }
        
        try {
            String testMessage = "这是一个性能测试消息";
            int testCount = 3; // 减少测试次数避免API限制
            
            long totalTime = 0;
            
            for (int i = 0; i < testCount; i++) {
                long startTime = System.currentTimeMillis();
                
                String response = tongyiAiService.chat(testMessage + " " + (i + 1));
                
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                totalTime += responseTime;
                
                log.info("第{}次请求响应时间: {}ms, 响应: {}", i + 1, responseTime, 
                        response.length() > 50 ? response.substring(0, 50) + "..." : response);
                
                // 添加延迟避免请求过于频繁
                Thread.sleep(2000);
            }
            
            long avgTime = totalTime / testCount;
            log.info("平均响应时间: {}ms", avgTime);
            
            // 验证响应时间合理（小于30秒）
            assertTrue(avgTime < 30000, "平均响应时间应该小于30秒");
            
        } catch (Exception e) {
            log.error("性能测试失败", e);
            fail("性能测试失败: " + e.getMessage());
        }
    }
}