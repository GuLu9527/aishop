package com.supermarket;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;

/**
 * Spring AI依赖测试类
 * 用于验证Spring AI相关类是否能正确导入
 */
public class SpringAiDependencyTest {
    
    /**
     * 测试ChatClient类是否可用
     */
    public void testChatClientImport() {
        // 这个方法只是为了验证类导入，不会实际执行
        ChatClient chatClient = null;
        System.out.println("ChatClient类导入成功");
    }
    
    /**
     * 测试ChatModel类是否可用
     */
    public void testChatModelImport() {
        // 这个方法只是为了验证类导入，不会实际执行
        ChatModel chatModel = null;
        System.out.println("ChatModel类导入成功");
    }
    
    /**
     * 测试ChatResponse类是否可用
     */
    public void testChatResponseImport() {
        // 这个方法只是为了验证类导入，不会实际执行
        ChatResponse chatResponse = null;
        System.out.println("ChatResponse类导入成功");
    }
    
    public static void main(String[] args) {
        SpringAiDependencyTest test = new SpringAiDependencyTest();
        test.testChatClientImport();
        test.testChatModelImport();
        test.testChatResponseImport();
        System.out.println("所有Spring AI类导入验证完成");
    }
}