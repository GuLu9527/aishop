package com.supermarket.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI服务配置类
 *
 * @author AI Assistant
 * @since 2024-01-01
 */
@Configuration
public class AiConfig {

    /**
     * 配置ChatClient Bean
     * 只有当ChatModel Bean存在时才创建
     */
    @Bean
    @ConditionalOnBean(ChatModel.class)
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
            .defaultSystem("你是一个专业的超市管理助手，能够帮助用户查询销售数据、管理库存、分析财务等业务问题。请用专业、友好的语气回答用户问题。")
            .build();
    }
}