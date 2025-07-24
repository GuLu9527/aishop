package com.supermarket.service;

import java.util.Map;

/**
 * AI意图识别服务接口
 */
public interface AiIntentService {
    
    /**
     * 解析用户输入的意图和实体
     * @param userInput 用户输入
     * @return 包含意图和实体的Map
     */
    Map<String, Object> parseIntent(String userInput);
    
    /**
     * 基于关键词匹配识别意图
     * @param userInput 用户输入
     * @return 意图名称
     */
    String matchIntentByKeywords(String userInput);
    
    /**
     * 提取实体信息
     * @param userInput 用户输入
     * @param intent 意图
     * @return 实体Map
     */
    Map<String, Object> extractEntities(String userInput, String intent);
}