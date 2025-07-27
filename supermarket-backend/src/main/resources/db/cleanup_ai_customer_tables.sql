-- =====================================================
-- AI客服功能清理脚本
-- 清理不需要的AI客服相关表，只保留基础AI聊天功能
-- =====================================================

-- 删除AI客服会话表
DROP TABLE IF EXISTS `ai_customer_session`;

-- 删除AI知识库表
DROP TABLE IF EXISTS `ai_knowledge_base`;

-- 删除AI服务评价表
DROP TABLE IF EXISTS `ai_service_evaluation`;

-- 删除AI工单表
DROP TABLE IF EXISTS `ai_service_ticket`;

-- 删除AI意图配置表（如果不需要意图识别）
DROP TABLE IF EXISTS `ai_intent_config`;

-- 删除AI分析结果表（暂时不需要）
DROP TABLE IF EXISTS `ai_analysis_result`;

-- 删除AI操作日志表（暂时不需要）
DROP TABLE IF EXISTS `ai_operation_log`;

-- 只保留以下核心AI表：
-- ai_conversation (AI对话会话表)
-- ai_message (AI消息表)

-- =====================================================
-- 验证清理结果
-- =====================================================
SHOW TABLES LIKE 'ai_%';