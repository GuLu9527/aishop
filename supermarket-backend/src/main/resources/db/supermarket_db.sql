/*
 Navicat Premium Dump SQL

 Source Server         : giao
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : localhost:3306
 Source Schema         : supermarket_db

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 26/07/2025 11:49:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_analysis_result
-- ----------------------------
DROP TABLE IF EXISTS `ai_analysis_result`;
CREATE TABLE `ai_analysis_result`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `analysis_type` tinyint NOT NULL COMMENT '分析类型：1-补货预测，2-销售趋势，3-商品关联，4-促销建议',
  `product_id` bigint NULL DEFAULT NULL COMMENT '关联商品ID',
  `input_data` json NULL COMMENT '输入数据JSON',
  `result_data` json NOT NULL COMMENT '分析结果JSON',
  `confidence_score` decimal(5, 2) NULL DEFAULT NULL COMMENT '置信度分数',
  `ai_service` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'AI服务：PAI/TongYi',
  `analysis_date` date NOT NULL COMMENT '分析日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-有效，0-已失效',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_analysis_type`(`analysis_type` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_analysis_date`(`analysis_date` ASC) USING BTREE,
  INDEX `idx_ai_service`(`ai_service` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI分析结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_conversation
-- ----------------------------
DROP TABLE IF EXISTS `ai_conversation`;
CREATE TABLE `ai_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话标题',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-进行中，2-已结束',
  `message_count` int NULL DEFAULT 0 COMMENT '消息总数',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_customer_session
-- ----------------------------
DROP TABLE IF EXISTS `ai_customer_session`;
CREATE TABLE `ai_customer_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话标识',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `customer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户姓名',
  `customer_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户联系方式',
  `session_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话标题',
  `status` tinyint NULL DEFAULT 1 COMMENT '会话状态：1-进行中，2-已结束，3-转人工',
  `message_count` int NULL DEFAULT 0 COMMENT '消息总数',
  `last_message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '最后一条消息内容',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `need_human_intervention` tinyint NULL DEFAULT 0 COMMENT '是否需要人工介入：0-否，1-是',
  `assigned_staff_id` bigint NULL DEFAULT NULL COMMENT '分配的客服人员ID',
  `assigned_staff_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分配的客服人员姓名',
  `customer_satisfaction` decimal(3, 2) NULL DEFAULT NULL COMMENT '客户满意度评分（1-5分）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_need_human_intervention`(`need_human_intervention` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI客服会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_intent_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_intent_config`;
CREATE TABLE `ai_intent_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `intent_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '意图名称',
  `intent_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '意图编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '意图描述',
  `keywords` json NULL COMMENT '关键词列表JSON',
  `api_mapping` json NULL COMMENT 'API映射配置JSON',
  `prompt_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提示词模板',
  `response_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '响应模板',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_intent_code`(`intent_code` ASC) USING BTREE,
  INDEX `idx_intent_name`(`intent_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI意图配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `ai_knowledge_base`;
CREATE TABLE `ai_knowledge_base`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '知识标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '知识内容',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '知识分类',
  `keywords` json NULL COMMENT '关键词列表（JSON格式）',
  `intent` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联意图',
  `priority` int NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
  `hit_count` int NULL DEFAULT 0 COMMENT '命中次数',
  `effectiveness_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '有效性评分（1-5分）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_intent`(`intent` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_hit_count`(`hit_count` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI知识库表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_message`;
CREATE TABLE `ai_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话标识',
  `message_type` tinyint NOT NULL COMMENT '消息类型：1-用户消息，2-AI回复，3-系统消息',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `intent` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '意图识别结果',
  `entities` json NULL COMMENT '实体提取结果JSON',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行的操作',
  `action_params` json NULL COMMENT '操作参数（JSON格式）',
  `action_result` json NULL COMMENT '操作结果',
  `model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AI模型名称',
  `process_time` bigint NULL DEFAULT NULL COMMENT '处理耗时（毫秒）',
  `satisfaction` tinyint NULL DEFAULT NULL COMMENT '用户满意度：1-5分',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'chat' COMMENT '消息来源：chat-普通聊天，customer_service-客服',
  `satisfaction_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '客户满意度评分（1-5分）',
  `need_human_intervention` tinyint NULL DEFAULT 0 COMMENT '是否需要人工介入：0-否，1-是',
  `knowledge_base_id` bigint NULL DEFAULT NULL COMMENT '关联的知识库ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_message_type`(`message_type` ASC) USING BTREE,
  INDEX `idx_intent`(`intent` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_source`(`source` ASC) USING BTREE,
  INDEX `idx_need_human_intervention`(`need_human_intervention` ASC) USING BTREE,
  INDEX `idx_knowledge_base_id`(`knowledge_base_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_operation_log`;
CREATE TABLE `ai_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作描述',
  `input_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户输入',
  `ai_response` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI响应',
  `api_calls` json NULL COMMENT '调用的API列表',
  `execution_status` tinyint NOT NULL COMMENT '执行状态：1-成功，2-失败，3-部分成功',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `execution_time` int NULL DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_execution_status`(`execution_status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_service_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `ai_service_evaluation`;
CREATE TABLE `ai_service_evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `evaluation_type` tinyint NOT NULL COMMENT '评价类型：1-AI服务评价，2-人工服务评价',
  `satisfaction_score` decimal(3, 2) NOT NULL COMMENT '满意度评分（1-5分）',
  `response_speed_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '响应速度评分（1-5分）',
  `solution_quality_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '解决质量评分（1-5分）',
  `service_attitude_score` decimal(3, 2) NULL DEFAULT NULL COMMENT '服务态度评分（1-5分）',
  `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '反馈内容',
  `improvement_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '改进建议',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_evaluation_type`(`evaluation_type` ASC) USING BTREE,
  INDEX `idx_satisfaction_score`(`satisfaction_score` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI客服评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ai_service_ticket
-- ----------------------------
DROP TABLE IF EXISTS `ai_service_ticket`;
CREATE TABLE `ai_service_ticket`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `ticket_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '工单编号',
  `session_id` bigint NULL DEFAULT NULL COMMENT '关联会话ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `customer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户姓名',
  `customer_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户联系方式',
  `problem_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题类型',
  `problem_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题标题',
  `problem_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题描述',
  `priority` tinyint NULL DEFAULT 2 COMMENT '优先级：1-低，2-中，3-高，4-紧急',
  `status` tinyint NULL DEFAULT 1 COMMENT '工单状态：1-待分配，2-处理中，3-待确认，4-已解决，5-已关闭',
  `assigned_to` bigint NULL DEFAULT NULL COMMENT '分配给（员工ID）',
  `assigned_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分配给（员工姓名）',
  `solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '解决方案',
  `resolved_time` datetime NULL DEFAULT NULL COMMENT '解决时间',
  `closed_time` datetime NULL DEFAULT NULL COMMENT '关闭时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_ticket_no`(`ticket_no` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_customer_id`(`customer_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_assigned_to`(`assigned_to` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI客服工单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for batch_operation_record
-- ----------------------------
DROP TABLE IF EXISTS `batch_operation_record`;
CREATE TABLE `batch_operation_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `batch_id` bigint NOT NULL COMMENT '批次ID',
  `operation_type` tinyint NOT NULL COMMENT '操作类型：1-入库，2-出库，3-调拨，4-盘点，5-报损',
  `quantity` int NOT NULL COMMENT '操作数量',
  `before_quantity` int NOT NULL COMMENT '操作前数量',
  `after_quantity` int NOT NULL COMMENT '操作后数量',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作原因',
  `reference_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联单号',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_operation_batch_time`(`batch_id` ASC, `create_time` ASC) USING BTREE,
  CONSTRAINT `fk_record_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batch` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '批次操作记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for batch_stock_detail
-- ----------------------------
DROP TABLE IF EXISTS `batch_stock_detail`;
CREATE TABLE `batch_stock_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `batch_id` bigint NOT NULL COMMENT '批次ID',
  `location_id` bigint NULL DEFAULT NULL COMMENT '库位ID',
  `quantity` int NOT NULL DEFAULT 0 COMMENT '数量',
  `reserved_quantity` int NOT NULL DEFAULT 0 COMMENT '预留数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_location`(`batch_id` ASC, `location_id` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_location_id`(`location_id` ASC) USING BTREE,
  CONSTRAINT `fk_detail_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batch` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '批次库存明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for finance_record
-- ----------------------------
DROP TABLE IF EXISTS `finance_record`;
CREATE TABLE `finance_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_type` tinyint NOT NULL COMMENT '记录类型：1-收入，2-支出',
  `business_type` tinyint NOT NULL COMMENT '业务类型：1-销售收入，2-采购支出，3-其他收入，4-其他支出，5-退货退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联订单号',
  `business_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '描述说明',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `operator_id` bigint NOT NULL COMMENT '操作员ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作员姓名',
  `record_date` datetime NOT NULL COMMENT '记录日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_business_type`(`business_type` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '财务记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for financial_record
-- ----------------------------
DROP TABLE IF EXISTS `financial_record`;
CREATE TABLE `financial_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `record_type` tinyint NOT NULL COMMENT '记录类型：1-收入，2-支出',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类：销售收入、采购支出、租金、水电费等',
  `amount` decimal(12, 2) NOT NULL COMMENT '金额',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `record_date` date NOT NULL COMMENT '记录日期',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for flyway_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history`  (
  `installed_rank` int NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int NULL DEFAULT NULL,
  `installed_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`) USING BTREE,
  INDEX `flyway_schema_history_s_idx`(`success` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for held_transaction
-- ----------------------------
DROP TABLE IF EXISTS `held_transaction`;
CREATE TABLE `held_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '挂单ID',
  `transaction_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂单号',
  `items_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品数据（JSON格式）',
  `item_count` int NOT NULL COMMENT '商品数量',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `cashier_id` bigint NOT NULL COMMENT '收银员ID',
  `cashier_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收银员姓名',
  `terminal_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收银机终端ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-挂起，2-已取单',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_transaction_no`(`transaction_no` ASC) USING BTREE,
  INDEX `idx_cashier_id`(`cashier_id` ASC) USING BTREE,
  INDEX `idx_terminal_id`(`terminal_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '挂单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for inventory_record
-- ----------------------------
DROP TABLE IF EXISTS `inventory_record`;
CREATE TABLE `inventory_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录单号',
  `record_type` tinyint NOT NULL COMMENT '记录类型：1-入库，2-出库，3-盘点，4-调拨，5-报损',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品条码',
  `change_quantity` int NOT NULL COMMENT '变动数量（正数为增加，负数为减少）',
  `before_quantity` int NOT NULL COMMENT '变动前库存',
  `after_quantity` int NOT NULL COMMENT '变动后库存',
  `unit_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '单价',
  `total_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '总金额',
  `supplier_id` bigint NULL DEFAULT NULL COMMENT '供应商ID（入库时使用）',
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商名称',
  `warehouse_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '仓库位置',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `production_date` datetime NULL DEFAULT NULL COMMENT '生产日期',
  `expiration_date` datetime NULL DEFAULT NULL COMMENT '过期日期',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作员ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作员姓名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `record_no`(`record_no` ASC) USING BTREE,
  INDEX `idx_record_no`(`record_no` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_expiration_date`(`expiration_date` ASC) USING BTREE,
  CONSTRAINT `inventory_record_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品条码',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `brand` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '品牌',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '个' COMMENT '单位',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `purchase_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '进货价',
  `selling_price` decimal(10, 2) NOT NULL COMMENT '销售价',
  `stock_quantity` int NULL DEFAULT 0 COMMENT '库存数量',
  `min_stock` int NULL DEFAULT 0 COMMENT '最低库存预警',
  `max_stock` int NULL DEFAULT 0 COMMENT '最高库存预警',
  `shelf_life_days` int NULL DEFAULT NULL COMMENT '保质期天数',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-在售，0-下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `batch_tracking` tinyint NULL DEFAULT 0 COMMENT '是否启用批次管理：1-启用，0-不启用',
  `expiry_tracking` tinyint NULL DEFAULT 0 COMMENT '是否启用过期日期跟踪：1-启用，0-不启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_barcode`(`barcode` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_product_name`(`product_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_production_date`(`production_date` ASC) USING BTREE,
  INDEX `idx_production_shelf_life`(`production_date` ASC, `shelf_life_days` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1946051776063545360 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表 - 已添加生产日期字段用于过期管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for product_batch
-- ----------------------------
DROP TABLE IF EXISTS `product_batch`;
CREATE TABLE `product_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `batch_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次号',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date NULL DEFAULT NULL COMMENT '过期日期',
  `supplier_id` bigint NULL DEFAULT NULL COMMENT '供应商ID',
  `purchase_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '进货价',
  `total_quantity` int NOT NULL DEFAULT 0 COMMENT '批次总数量',
  `available_quantity` int NOT NULL DEFAULT 0 COMMENT '可用数量',
  `reserved_quantity` int NOT NULL DEFAULT 0 COMMENT '预留数量',
  `sold_quantity` int NOT NULL DEFAULT 0 COMMENT '已售数量',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-临期，3-过期，0-停用',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_batch`(`product_id` ASC, `batch_code` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_batch_code`(`batch_code` ASC) USING BTREE,
  INDEX `idx_expiry_date`(`expiry_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_batch_expiry_status`(`expiry_date` ASC, `status` ASC, `available_quantity` ASC) USING BTREE,
  INDEX `idx_batch_product_expiry`(`product_id` ASC, `expiry_date` ASC, `status` ASC) USING BTREE,
  CONSTRAINT `fk_batch_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品批次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for promotion
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `promotion_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动名称',
  `promotion_type` tinyint NOT NULL COMMENT '活动类型：1-满减，2-折扣，3-买赠，4-第二件半价',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `rule_config` json NULL COMMENT '规则配置JSON',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-进行中，2-已结束，0-已停用',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_end_time`(`end_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '促销活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for promotion_product
-- ----------------------------
DROP TABLE IF EXISTS `promotion_product`;
CREATE TABLE `promotion_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `promotion_id` bigint NOT NULL COMMENT '促销活动ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `discount_type` tinyint NULL DEFAULT NULL COMMENT '折扣类型：1-固定金额，2-百分比',
  `discount_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '折扣值',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_promotion_product`(`promotion_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_promotion_id`(`promotion_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '促销商品关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `supplier_id` bigint NOT NULL COMMENT '供货商ID',
  `order_date` date NOT NULL COMMENT '订单日期',
  `expected_date` date NULL DEFAULT NULL COMMENT '预期到货日期',
  `actual_date` date NULL DEFAULT NULL COMMENT '实际到货日期',
  `total_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '订单总金额',
  `paid_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '已付金额',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-待确认，2-已确认，3-已发货，4-已收货，5-已完成',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_order_date`(`order_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for purchase_order_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order_item`;
CREATE TABLE `purchase_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '采购数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_price` decimal(12, 2) NOT NULL COMMENT '小计',
  `received_quantity` int NULL DEFAULT 0 COMMENT '已收货数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sale
-- ----------------------------
DROP TABLE IF EXISTS `sale`;
CREATE TABLE `sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sale_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '销售单号',
  `sale_type` tinyint NOT NULL DEFAULT 1 COMMENT '销售类型：1-正常销售，2-退货',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '销售总金额',
  `paid_amount` decimal(10, 2) NOT NULL COMMENT '实收金额',
  `change_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '找零金额',
  `payment_method` tinyint NOT NULL COMMENT '支付方式：1-现金，2-支付宝，3-微信，4-银行卡',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '销售状态：1-已完成，2-已退货，3-部分退货',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作员ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作员姓名',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sale_no`(`sale_no` ASC) USING BTREE,
  INDEX `idx_sale_no`(`sale_no` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sale_item
-- ----------------------------
DROP TABLE IF EXISTS `sale_item`;
CREATE TABLE `sale_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sale_id` bigint NOT NULL COMMENT '销售订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品条码',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '销售单价',
  `quantity` int NOT NULL COMMENT '销售数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `discount_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '折扣金额',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实际金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sale_id`(`sale_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `sale_item_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sale` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sale_order
-- ----------------------------
DROP TABLE IF EXISTS `sale_order`;
CREATE TABLE `sale_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '应收金额',
  `received_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实收金额',
  `change_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '找零金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式：cash-现金，card-刷卡，alipay-支付宝，wechat-微信',
  `total_quantity` int NOT NULL COMMENT '商品总数量',
  `cashier_id` bigint NOT NULL COMMENT '收银员ID',
  `cashier_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收银员姓名',
  `terminal_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收银机终端ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '订单状态：1-已完成，2-已退款，3-部分退款',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `order_date` date GENERATED ALWAYS AS (cast(`create_time` as date)) STORED COMMENT '订单日期' NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_cashier_id`(`cashier_id` ASC) USING BTREE,
  INDEX `idx_terminal_id`(`terminal_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_payment_method`(`payment_method` ASC) USING BTREE,
  INDEX `idx_order_date`(`order_date` ASC) USING BTREE,
  INDEX `idx_status_date`(`status` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sale_order_item
-- ----------------------------
DROP TABLE IF EXISTS `sale_order_item`;
CREATE TABLE `sale_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品条码',
  `selling_price` decimal(10, 2) NOT NULL COMMENT '销售价格',
  `quantity` int NOT NULL COMMENT '购买数量',
  `unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_barcode`(`barcode` ASC) USING BTREE,
  CONSTRAINT `fk_sale_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `sale_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_alert
-- ----------------------------
DROP TABLE IF EXISTS `stock_alert`;
CREATE TABLE `stock_alert`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `alert_type` tinyint NOT NULL COMMENT '预警类型：1-低库存，2-高库存，3-临期商品',
  `current_stock` int NOT NULL COMMENT '当前库存',
  `threshold_value` int NULL DEFAULT NULL COMMENT '预警阈值',
  `expire_date` date NULL DEFAULT NULL COMMENT '过期日期（临期商品）',
  `alert_message` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警信息',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-未处理，2-已处理，0-已忽略',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `handle_remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `batch_id` bigint NULL DEFAULT NULL COMMENT '批次ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_alert_type`(`alert_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  CONSTRAINT `fk_alert_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batch` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存预警记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_check
-- ----------------------------
DROP TABLE IF EXISTS `stock_check`;
CREATE TABLE `stock_check`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `check_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '盘点单号',
  `check_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '盘点名称',
  `check_type` tinyint NOT NULL COMMENT '盘点类型：1-全盘，2-抽盘，3-循环盘点',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '盘点状态：1-进行中，2-已完成，3-已取消',
  `start_time` datetime NULL DEFAULT NULL COMMENT '盘点开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '盘点结束时间',
  `total_products` int NULL DEFAULT 0 COMMENT '盘点商品总数',
  `checked_products` int NULL DEFAULT 0 COMMENT '已盘点商品数',
  `difference_products` int NULL DEFAULT 0 COMMENT '盈亏商品数',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人姓名',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人姓名',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `check_no`(`check_no` ASC) USING BTREE,
  INDEX `idx_check_no`(`check_no` ASC) USING BTREE,
  INDEX `idx_check_type`(`check_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存盘点表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_check_item
-- ----------------------------
DROP TABLE IF EXISTS `stock_check_item`;
CREATE TABLE `stock_check_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `check_id` bigint NOT NULL COMMENT '盘点ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品条码',
  `book_quantity` int NOT NULL COMMENT '账面库存',
  `actual_quantity` int NULL DEFAULT 0 COMMENT '实际库存',
  `difference_quantity` int NULL DEFAULT 0 COMMENT '盈亏数量',
  `unit_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品单价',
  `difference_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '盈亏金额',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '盘点状态：1-待盘点，2-已盘点，3-有差异',
  `checker_id` bigint NULL DEFAULT NULL COMMENT '盘点人ID',
  `checker_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '盘点人姓名',
  `check_time` datetime NULL DEFAULT NULL COMMENT '盘点时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_check_id`(`check_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `stock_check_item_ibfk_1` FOREIGN KEY (`check_id`) REFERENCES `stock_check` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `stock_check_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存盘点明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for stock_record
-- ----------------------------
DROP TABLE IF EXISTS `stock_record`;
CREATE TABLE `stock_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `change_type` tinyint NOT NULL COMMENT '变动类型：1-入库，2-出库，3-盘点，4-损耗',
  `change_quantity` int NOT NULL COMMENT '变动数量',
  `before_quantity` int NOT NULL COMMENT '变动前数量',
  `after_quantity` int NOT NULL COMMENT '变动后数量',
  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变动原因',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `expire_date` date NULL DEFAULT NULL COMMENT '过期日期',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `batch_id` bigint NULL DEFAULT NULL COMMENT '批次ID',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date NULL DEFAULT NULL COMMENT '过期日期',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_change_type`(`change_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  CONSTRAINT `fk_stock_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batch` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存变动记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '供货商ID',
  `supplier_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供货商名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `credit_rating` tinyint NULL DEFAULT 5 COMMENT '信用等级：1-5星',
  `payment_terms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '付款条件',
  `delivery_cycle` int NULL DEFAULT NULL COMMENT '供货周期（天）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_supplier_name`(`supplier_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供货商表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for supplier_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `supplier_evaluation`;
CREATE TABLE `supplier_evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `supplier_id` bigint NOT NULL COMMENT '供货商ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID',
  `quality_score` tinyint NOT NULL COMMENT '质量评分：1-5分',
  `delivery_score` tinyint NOT NULL COMMENT '及时性评分：1-5分',
  `price_score` tinyint NOT NULL COMMENT '价格评分：1-5分',
  `service_score` tinyint NOT NULL COMMENT '服务评分：1-5分',
  `overall_score` decimal(3, 1) NOT NULL COMMENT '综合评分',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评价内容',
  `evaluator_id` bigint NULL DEFAULT NULL COMMENT '评价人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_overall_score`(`overall_score` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供货商评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for supplier_product
-- ----------------------------
DROP TABLE IF EXISTS `supplier_product`;
CREATE TABLE `supplier_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `supplier_id` bigint NOT NULL COMMENT '供货商ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `supply_price` decimal(10, 2) NOT NULL COMMENT '供货价格',
  `min_order_quantity` int NULL DEFAULT 1 COMMENT '最小起订量',
  `delivery_days` int NULL DEFAULT 7 COMMENT '供货周期（天）',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常供货，0-停止供货',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_supplier_product`(`supplier_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_supplier_id`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供货商商品关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '配置值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1945391279349800963 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- View structure for v_batch_stock_summary
-- ----------------------------
DROP VIEW IF EXISTS `v_batch_stock_summary`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_batch_stock_summary` AS select `pb`.`id` AS `batch_id`,`pb`.`product_id` AS `product_id`,`p`.`product_name` AS `product_name`,`p`.`barcode` AS `barcode`,`pb`.`batch_code` AS `batch_code`,`pb`.`production_date` AS `production_date`,`pb`.`expiry_date` AS `expiry_date`,`pb`.`available_quantity` AS `available_quantity`,`pb`.`reserved_quantity` AS `reserved_quantity`,`pb`.`sold_quantity` AS `sold_quantity`,`pb`.`status` AS `status`,(to_days(`pb`.`expiry_date`) - to_days(curdate())) AS `remaining_days`,(case when (`pb`.`expiry_date` < curdate()) then '已过期' when ((to_days(`pb`.`expiry_date`) - to_days(curdate())) <= 3) then '即将过期' when ((to_days(`pb`.`expiry_date`) - to_days(curdate())) <= 7) then '临期' else '正常' end) AS `expiry_status` from (`product_batch` `pb` left join `product` `p` on((`pb`.`product_id` = `p`.`id`))) where (`pb`.`status` = 1);

-- ----------------------------
-- View structure for v_expiring_products
-- ----------------------------
DROP VIEW IF EXISTS `v_expiring_products`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_expiring_products` AS select `p`.`id` AS `product_id`,`p`.`product_name` AS `product_name`,`p`.`barcode` AS `barcode`,count(`pb`.`id`) AS `batch_count`,sum(`pb`.`available_quantity`) AS `total_quantity`,min(`pb`.`expiry_date`) AS `earliest_expiry`,min((to_days(`pb`.`expiry_date`) - to_days(curdate()))) AS `min_remaining_days` from (`product` `p` join `product_batch` `pb` on((`p`.`id` = `pb`.`product_id`))) where ((`pb`.`status` = 1) and (`pb`.`expiry_date` is not null) and (`pb`.`expiry_date` >= curdate()) and ((to_days(`pb`.`expiry_date`) - to_days(curdate())) <= 7)) group by `p`.`id`,`p`.`product_name`,`p`.`barcode` order by `min_remaining_days`;

SET FOREIGN_KEY_CHECKS = 1;
