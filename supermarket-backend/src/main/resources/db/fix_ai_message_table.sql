-- 修复ai_message表结构，添加缺失的字段和修复字段约束
USE supermarket_db;

-- 首先检查表结构
DESCRIBE ai_message;

-- 添加user_id字段（如果不存在）
ALTER TABLE ai_message ADD COLUMN IF NOT EXISTS user_id BIGINT COMMENT '用户ID' AFTER content;

-- 添加user_name字段（如果不存在）
ALTER TABLE ai_message ADD COLUMN IF NOT EXISTS user_name VARCHAR(50) COMMENT '用户姓名' AFTER user_id;

-- 修复conversation_id字段，允许NULL或设置默认值
ALTER TABLE ai_message MODIFY COLUMN conversation_id BIGINT NULL COMMENT '对话ID';

-- 或者设置默认值为0
-- ALTER TABLE ai_message MODIFY COLUMN conversation_id BIGINT DEFAULT 0 COMMENT '对话ID';

-- 添加索引（如果不存在）
ALTER TABLE ai_message ADD INDEX IF NOT EXISTS idx_user_id (user_id);
ALTER TABLE ai_message ADD INDEX IF NOT EXISTS idx_conversation_id (conversation_id);

-- 查看修复后的表结构
DESCRIBE ai_message;

-- 显示表的创建语句以确认结构
SHOW CREATE TABLE ai_message;
