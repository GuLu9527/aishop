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

 Date: 19/07/2025 13:40:08
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI分析结果表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_analysis_result
-- ----------------------------

-- ----------------------------
-- Table structure for ai_conversation
-- ----------------------------
DROP TABLE IF EXISTS `ai_conversation`;
CREATE TABLE `ai_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话标识',
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_conversation
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI意图配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_intent_config
-- ----------------------------

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
  `intent` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '识别的意图',
  `entities` json NULL COMMENT '提取的实体（JSON格式）',
  `action` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行的操作',
  `action_params` json NULL COMMENT '操作参数（JSON格式）',
  `action_result` json NULL COMMENT '操作结果',
  `model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AI模型名称',
  `process_time` bigint NULL DEFAULT NULL COMMENT '处理耗时（毫秒）',
  `satisfaction` tinyint NULL DEFAULT NULL COMMENT '用户满意度：1-5分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_message_type`(`message_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI消息记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_message
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_operation_log
-- ----------------------------

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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_record_type`(`record_type` ASC) USING BTREE,
  INDEX `idx_record_date`(`record_date` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '财务记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of financial_record
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '挂单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of held_transaction
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory_record
-- ----------------------------

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
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-在售，0-下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_barcode`(`barcode` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_product_name`(`product_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1946051776063545359 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1946048310675107841, '烤肠', '6907987232367', 1, '金锣', '个', '100g', 2.00, 4.00, 50, 10, 100, 30, '', '热食', NULL, 1, '2025-07-18 11:24:08', '2025-07-18 11:24:08', NULL, NULL);
INSERT INTO `product` VALUES (1946051776063545345, '面包', '6909836858016', 1, '桃李', '个', '250g', 2.00, 6.00, 20, 5, 40, 15, '', '测试', NULL, 1, '2025-07-18 11:37:54', '2025-07-18 11:37:54', 1, 1);
INSERT INTO `product` VALUES (1946051776063545346, '可口可乐', '6901668001234', 6, '可口可乐', '瓶', '330ml', 2.50, 3.50, 101, 20, 500, 365, NULL, '', '可口可乐公司', 1, '2025-07-18 14:39:06', '2025-07-18 16:23:53', 1, 1);
INSERT INTO `product` VALUES (1946051776063545347, '康师傅方便面', '6901668005678', 7, '康师傅', '包', '100g', 3.00, 4.50, 82, 15, 300, 180, NULL, NULL, '康师傅控股有限公司', 1, '2025-07-18 14:39:06', '2025-07-18 20:36:05', 1, 1);
INSERT INTO `product` VALUES (1946051776063545348, '海天生抽', '6901668009012', 8, '海天', '瓶', '500ml', 8.00, 12.00, 52, 10, 200, 720, NULL, NULL, '佛山市海天调味食品股份有限公司', 1, '2025-07-18 14:39:06', '2025-07-18 20:36:05', 1, 1);
INSERT INTO `product` VALUES (1946051776063545349, '立白洗洁精', '6901668003456', 9, '立白', '瓶', '1L', 6.00, 9.50, 62, 12, 250, 1095, NULL, NULL, '广州立白企业集团有限公司', 1, '2025-07-18 14:39:06', '2025-07-18 20:36:05', 1, 1);
INSERT INTO `product` VALUES (1946051776063545350, '飘柔洗发水', '6901668007890', 10, '飘柔', '瓶', '400ml', 15.00, 22.00, 42, 8, 150, 1095, NULL, NULL, '宝洁公司', 1, '2025-07-18 14:39:06', '2025-07-18 20:36:05', 1, 1);
INSERT INTO `product` VALUES (1946051776063545351, '馒头', '7889457861', 1, '杂牌', '个', '小包装', 4.00, 10.00, 6, 3, 10, 7, NULL, '测试商品', '无', 1, '2025-07-18 14:47:12', '2025-07-18 17:30:23', 1, 1);
INSERT INTO `product` VALUES (1946051776063545358, '面条', '78945678154', 1, '自研', '袋', '小包装', 1.00, 3.00, 28, 28, 50, 365, NULL, '测试商品', '自产', 1, '2025-07-18 17:56:47', '2025-07-18 20:27:52', 1, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, '食品饮料', 0, '各类食品和饮料', 1, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (2, '日用百货', 0, '日常生活用品', 2, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (3, '个人护理', 0, '个人护理用品', 3, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (4, '家居用品', 0, '家居生活用品', 4, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (5, '文具办公', 0, '文具和办公用品', 5, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (6, '饮料', 1, '各类饮料', 1, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (7, '零食', 1, '各类零食', 2, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (8, '调料', 1, '调味料', 3, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (9, '清洁用品', 2, '清洁用品', 1, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');
INSERT INTO `product_category` VALUES (10, '洗护用品', 3, '洗发护发用品', 1, 1, '2025-07-18 14:39:06', '2025-07-18 14:39:06');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '促销活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of promotion
-- ----------------------------

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
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_promotion_product`(`promotion_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_promotion_id`(`promotion_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '促销商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of promotion_product
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_order
-- ----------------------------

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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '采购订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of purchase_order_item
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale
-- ----------------------------
INSERT INTO `sale` VALUES (1, 'SO20240117001', 1, 156.80, 160.00, 3.20, 1, 1, 1, '管理员', '现金支付', '2024-01-17 09:15:30', '2024-01-17 09:15:30');
INSERT INTO `sale` VALUES (2, 'SO20240117002', 1, 89.50, 89.50, 0.00, 2, 1, 1, '管理员', '支付宝支付', '2024-01-17 10:22:15', '2024-01-17 10:22:15');
INSERT INTO `sale` VALUES (3, 'SO20240117003', 1, 234.20, 234.20, 0.00, 3, 1, 1, '管理员', '微信支付', '2024-01-17 11:45:20', '2024-01-17 11:45:20');
INSERT INTO `sale` VALUES (4, 'SO20240117004', 1, 67.30, 70.00, 2.70, 1, 1, 1, '管理员', '现金支付', '2024-01-17 14:18:45', '2024-01-17 14:18:45');
INSERT INTO `sale` VALUES (5, 'SO20240117005', 1, 123.90, 123.90, 0.00, 4, 1, 1, '管理员', '银行卡支付', '2024-01-17 15:33:10', '2024-01-17 15:33:10');
INSERT INTO `sale` VALUES (6, 'SO20240117006', 1, 45.60, 50.00, 4.40, 1, 1, 1, '管理员', '现金支付', '2024-01-17 16:20:35', '2024-01-17 16:20:35');
INSERT INTO `sale` VALUES (7, 'SO20240117007', 1, 198.75, 198.75, 0.00, 2, 1, 1, '管理员', '支付宝支付', '2024-01-17 17:55:20', '2024-01-17 17:55:20');
INSERT INTO `sale` VALUES (8, 'SO20240117008', 1, 78.40, 78.40, 0.00, 3, 1, 1, '管理员', '微信支付', '2024-01-17 19:12:15', '2024-01-17 19:12:15');
INSERT INTO `sale` VALUES (9, 'SO20240116001', 1, 145.30, 150.00, 4.70, 1, 1, 1, '管理员', '现金支付', '2024-01-16 09:30:20', '2024-01-16 09:30:20');
INSERT INTO `sale` VALUES (10, 'SO20240116002', 1, 92.80, 92.80, 0.00, 2, 1, 1, '管理员', '支付宝支付', '2024-01-16 11:15:45', '2024-01-16 11:15:45');
INSERT INTO `sale` VALUES (11, 'SO20240116003', 1, 267.50, 267.50, 0.00, 3, 1, 1, '管理员', '微信支付', '2024-01-16 13:22:30', '2024-01-16 13:22:30');
INSERT INTO `sale` VALUES (12, 'SO20240116004', 1, 56.20, 60.00, 3.80, 1, 1, 1, '管理员', '现金支付', '2024-01-16 15:45:10', '2024-01-16 15:45:10');
INSERT INTO `sale` VALUES (13, 'SO20240116005', 1, 189.90, 189.90, 0.00, 4, 1, 1, '管理员', '银行卡支付', '2024-01-16 17:30:25', '2024-01-16 17:30:25');
INSERT INTO `sale` VALUES (14, 'SO20240115001', 1, 112.40, 112.40, 0.00, 2, 1, 1, '管理员', '支付宝支付', '2024-01-15 10:20:15', '2024-01-15 10:20:15');
INSERT INTO `sale` VALUES (15, 'SO20240115002', 1, 203.60, 210.00, 6.40, 1, 1, 1, '管理员', '现金支付', '2024-01-15 14:35:40', '2024-01-15 14:35:40');
INSERT INTO `sale` VALUES (16, 'SO20240115003', 1, 87.30, 87.30, 0.00, 3, 1, 1, '管理员', '微信支付', '2024-01-15 16:50:20', '2024-01-15 16:50:20');

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
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale_item
-- ----------------------------
INSERT INTO `sale_item` VALUES (1, 1, 1, '可口可乐 330ml', '6901234567890', 3.50, 2, 7.00, 0.00, 7.00, '2024-01-17 09:15:30', '2024-01-17 09:15:30');
INSERT INTO `sale_item` VALUES (2, 1, 2, '康师傅红烧牛肉面', '6901234567891', 4.80, 3, 14.40, 0.00, 14.40, '2024-01-17 09:15:30', '2024-01-17 09:15:30');
INSERT INTO `sale_item` VALUES (3, 1, 3, '金龙鱼调和油 5L', '6901234567892', 45.20, 3, 135.60, 0.20, 135.40, '2024-01-17 09:15:30', '2024-01-17 09:15:30');
INSERT INTO `sale_item` VALUES (4, 2, 4, '蒙牛纯牛奶 250ml*12', '6901234567893', 28.50, 2, 57.00, 0.00, 57.00, '2024-01-17 10:22:15', '2024-01-17 10:22:15');
INSERT INTO `sale_item` VALUES (5, 2, 5, '奥利奥饼干 116g', '6901234567894', 8.90, 2, 17.80, 0.00, 17.80, '2024-01-17 10:22:15', '2024-01-17 10:22:15');
INSERT INTO `sale_item` VALUES (6, 2, 6, '海天生抽 500ml', '6901234567895', 7.30, 2, 14.60, 0.00, 14.60, '2024-01-17 10:22:15', '2024-01-17 10:22:15');
INSERT INTO `sale_item` VALUES (7, 3, 7, '五常大米 5kg', '6901234567896', 35.80, 4, 143.20, 0.00, 143.20, '2024-01-17 11:45:20', '2024-01-17 11:45:20');
INSERT INTO `sale_item` VALUES (8, 3, 8, '农夫山泉 550ml*24', '6901234567897', 45.50, 2, 91.00, 0.00, 91.00, '2024-01-17 11:45:20', '2024-01-17 11:45:20');
INSERT INTO `sale_item` VALUES (9, 4, 9, '双汇火腿肠 30g*10', '6901234567898', 12.80, 3, 38.40, 0.00, 38.40, '2024-01-17 14:18:45', '2024-01-17 14:18:45');
INSERT INTO `sale_item` VALUES (10, 4, 10, '统一绿茶 500ml', '6901234567899', 2.90, 10, 29.00, 0.10, 28.90, '2024-01-17 14:18:45', '2024-01-17 14:18:45');
INSERT INTO `sale_item` VALUES (11, 5, 1, '可口可乐 330ml', '6901234567890', 3.50, 5, 17.50, 0.00, 17.50, '2024-01-17 15:33:10', '2024-01-17 15:33:10');
INSERT INTO `sale_item` VALUES (12, 5, 3, '金龙鱼调和油 5L', '6901234567892', 45.20, 2, 90.40, 0.00, 90.40, '2024-01-17 15:33:10', '2024-01-17 15:33:10');
INSERT INTO `sale_item` VALUES (13, 5, 7, '五常大米 5kg', '6901234567896', 35.80, 1, 35.80, 0.00, 35.80, '2024-01-17 15:33:10', '2024-01-17 15:33:10');
INSERT INTO `sale_item` VALUES (14, 6, 2, '康师傅红烧牛肉面', '6901234567891', 4.80, 4, 19.20, 0.00, 19.20, '2024-01-17 16:20:35', '2024-01-17 16:20:35');
INSERT INTO `sale_item` VALUES (15, 6, 5, '奥利奥饼干 116g', '6901234567894', 8.90, 3, 26.70, 0.30, 26.40, '2024-01-17 16:20:35', '2024-01-17 16:20:35');
INSERT INTO `sale_item` VALUES (16, 7, 4, '蒙牛纯牛奶 250ml*12', '6901234567893', 28.50, 4, 114.00, 0.00, 114.00, '2024-01-17 17:55:20', '2024-01-17 17:55:20');
INSERT INTO `sale_item` VALUES (17, 7, 8, '农夫山泉 550ml*24', '6901234567897', 45.50, 1, 45.50, 0.00, 45.50, '2024-01-17 17:55:20', '2024-01-17 17:55:20');
INSERT INTO `sale_item` VALUES (18, 7, 6, '海天生抽 500ml', '6901234567895', 7.30, 5, 36.50, 0.00, 36.50, '2024-01-17 17:55:20', '2024-01-17 17:55:20');
INSERT INTO `sale_item` VALUES (19, 8, 9, '双汇火腿肠 30g*10', '6901234567898', 12.80, 2, 25.60, 0.00, 25.60, '2024-01-17 19:12:15', '2024-01-17 19:12:15');
INSERT INTO `sale_item` VALUES (20, 8, 10, '统一绿茶 500ml', '6901234567899', 2.90, 18, 52.20, 0.40, 51.80, '2024-01-17 19:12:15', '2024-01-17 19:12:15');
INSERT INTO `sale_item` VALUES (21, 9, 1, '可口可乐 330ml', '6901234567890', 3.50, 8, 28.00, 0.00, 28.00, '2024-01-16 09:30:20', '2024-01-16 09:30:20');
INSERT INTO `sale_item` VALUES (22, 9, 2, '康师傅红烧牛肉面', '6901234567891', 4.80, 6, 28.80, 0.00, 28.80, '2024-01-16 09:30:20', '2024-01-16 09:30:20');
INSERT INTO `sale_item` VALUES (23, 9, 3, '金龙鱼调和油 5L', '6901234567892', 45.20, 2, 90.40, 1.90, 88.50, '2024-01-16 09:30:20', '2024-01-16 09:30:20');
INSERT INTO `sale_item` VALUES (24, 10, 4, '蒙牛纯牛奶 250ml*12', '6901234567893', 28.50, 2, 57.00, 0.00, 57.00, '2024-01-16 11:15:45', '2024-01-16 11:15:45');
INSERT INTO `sale_item` VALUES (25, 10, 7, '五常大米 5kg', '6901234567896', 35.80, 1, 35.80, 0.00, 35.80, '2024-01-16 11:15:45', '2024-01-16 11:15:45');
INSERT INTO `sale_item` VALUES (26, 11, 8, '农夫山泉 550ml*24', '6901234567897', 45.50, 3, 136.50, 0.00, 136.50, '2024-01-16 13:22:30', '2024-01-16 13:22:30');
INSERT INTO `sale_item` VALUES (27, 11, 9, '双汇火腿肠 30g*10', '6901234567898', 12.80, 5, 64.00, 0.00, 64.00, '2024-01-16 13:22:30', '2024-01-16 13:22:30');
INSERT INTO `sale_item` VALUES (28, 11, 10, '统一绿茶 500ml', '6901234567899', 2.90, 23, 66.70, 0.30, 66.40, '2024-01-16 13:22:30', '2024-01-16 13:22:30');
INSERT INTO `sale_item` VALUES (29, 12, 5, '奥利奥饼干 116g', '6901234567894', 8.90, 4, 35.60, 0.00, 35.60, '2024-01-16 15:45:10', '2024-01-16 15:45:10');
INSERT INTO `sale_item` VALUES (30, 12, 6, '海天生抽 500ml', '6901234567895', 7.30, 3, 21.90, 1.30, 20.60, '2024-01-16 15:45:10', '2024-01-16 15:45:10');
INSERT INTO `sale_item` VALUES (31, 13, 1, '可口可乐 330ml', '6901234567890', 3.50, 12, 42.00, 0.00, 42.00, '2024-01-16 17:30:25', '2024-01-16 17:30:25');
INSERT INTO `sale_item` VALUES (32, 13, 3, '金龙鱼调和油 5L', '6901234567892', 45.20, 3, 135.60, 0.00, 135.60, '2024-01-16 17:30:25', '2024-01-16 17:30:25');
INSERT INTO `sale_item` VALUES (33, 13, 7, '五常大米 5kg', '6901234567896', 35.80, 1, 35.80, 23.50, 12.30, '2024-01-16 17:30:25', '2024-01-16 17:30:25');
INSERT INTO `sale_item` VALUES (34, 14, 2, '康师傅红烧牛肉面', '6901234567891', 4.80, 8, 38.40, 0.00, 38.40, '2024-01-15 10:20:15', '2024-01-15 10:20:15');
INSERT INTO `sale_item` VALUES (35, 14, 4, '蒙牛纯牛奶 250ml*12', '6901234567893', 28.50, 2, 57.00, 0.00, 57.00, '2024-01-15 10:20:15', '2024-01-15 10:20:15');
INSERT INTO `sale_item` VALUES (36, 14, 10, '统一绿茶 500ml', '6901234567899', 2.90, 6, 17.40, 0.40, 17.00, '2024-01-15 10:20:15', '2024-01-15 10:20:15');
INSERT INTO `sale_item` VALUES (37, 15, 8, '农夫山泉 550ml*24', '6901234567897', 45.50, 2, 91.00, 0.00, 91.00, '2024-01-15 14:35:40', '2024-01-15 14:35:40');
INSERT INTO `sale_item` VALUES (38, 15, 3, '金龙鱼调和油 5L', '6901234567892', 45.20, 2, 90.40, 0.00, 90.40, '2024-01-15 14:35:40', '2024-01-15 14:35:40');
INSERT INTO `sale_item` VALUES (39, 15, 6, '海天生抽 500ml', '6901234567895', 7.30, 3, 21.90, 0.70, 21.20, '2024-01-15 14:35:40', '2024-01-15 14:35:40');
INSERT INTO `sale_item` VALUES (40, 16, 5, '奥利奥饼干 116g', '6901234567894', 8.90, 5, 44.50, 0.00, 44.50, '2024-01-15 16:50:20', '2024-01-15 16:50:20');
INSERT INTO `sale_item` VALUES (41, 16, 9, '双汇火腿肠 30g*10', '6901234567898', 12.80, 2, 25.60, 0.00, 25.60, '2024-01-15 16:50:20', '2024-01-15 16:50:20');
INSERT INTO `sale_item` VALUES (42, 16, 1, '可口可乐 330ml', '6901234567890', 3.50, 5, 17.50, 0.30, 17.20, '2024-01-15 16:50:20', '2024-01-15 16:50:20');

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale_order
-- ----------------------------
INSERT INTO `sale_order` VALUES (1, 'SO20240101001', 25.50, 30.00, 4.50, 'cash', 3, 1, '张三', 'POS001', 1, NULL, '2025-07-18 20:50:47', '2025-07-18 20:50:47', DEFAULT);
INSERT INTO `sale_order` VALUES (2, 'SO20240101002', 18.80, 18.80, 0.00, 'alipay', 2, 1, '张三', 'POS001', 1, NULL, '2025-07-18 20:50:47', '2025-07-18 20:50:47', DEFAULT);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '销售订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sale_order_item
-- ----------------------------
INSERT INTO `sale_order_item` VALUES (1, 1, 1, '可口可乐', '6901668001234', 3.50, 2, '瓶', 7.00, '2025-07-18 20:50:47');
INSERT INTO `sale_order_item` VALUES (2, 1, 2, '康师傅方便面', '6901668005678', 4.50, 1, '包', 4.50, '2025-07-18 20:50:47');
INSERT INTO `sale_order_item` VALUES (3, 1, 3, '旺旺雪饼', '6901668009876', 14.00, 1, '包', 14.00, '2025-07-18 20:50:47');
INSERT INTO `sale_order_item` VALUES (4, 2, 1, '可口可乐', '6901668001234', 3.50, 1, '瓶', 3.50, '2025-07-18 20:50:47');
INSERT INTO `sale_order_item` VALUES (5, 2, 4, '统一绿茶', '6901668012345', 15.30, 1, '瓶', 15.30, '2025-07-18 20:50:47');

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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_alert_type`(`alert_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存预警记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_alert
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存盘点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_check
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存盘点明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_check_item
-- ----------------------------

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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_change_type`(`change_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存变动记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of stock_record
-- ----------------------------
INSERT INTO `stock_record` VALUES (1, 1946051776063545358, 1, 20, 0, 20, NULL, NULL, '初始入库', NULL, NULL, 1, '2025-07-18 17:56:47');
INSERT INTO `stock_record` VALUES (2, 1946051776063545358, 1, 2, 20, 22, NULL, NULL, '123', NULL, NULL, 1, '2025-07-18 19:15:26');
INSERT INTO `stock_record` VALUES (3, 1946051776063545358, 1, 1, 22, 23, NULL, NULL, '测试', NULL, NULL, 1, '2025-07-18 19:16:46');
INSERT INTO `stock_record` VALUES (4, 1946051776063545358, 1, 2, 23, 25, NULL, NULL, '测试', NULL, NULL, 1, '2025-07-18 20:07:01');
INSERT INTO `stock_record` VALUES (5, 1946051776063545358, 1, 3, 25, 28, NULL, NULL, '测试', NULL, NULL, 1, '2025-07-18 20:07:08');
INSERT INTO `stock_record` VALUES (6, 1946051776063545347, 1, 2, 80, 82, NULL, NULL, '测试批量调整', NULL, NULL, 1, '2025-07-18 20:36:05');
INSERT INTO `stock_record` VALUES (7, 1946051776063545348, 1, 2, 50, 52, NULL, NULL, '测试批量调整', NULL, NULL, 1, '2025-07-18 20:36:05');
INSERT INTO `stock_record` VALUES (8, 1946051776063545349, 1, 2, 60, 62, NULL, NULL, '测试批量调整', NULL, NULL, 1, '2025-07-18 20:36:05');
INSERT INTO `stock_record` VALUES (9, 1946051776063545350, 1, 2, 40, 42, NULL, NULL, '测试批量调整', NULL, NULL, 1, '2025-07-18 20:36:05');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供货商表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier
-- ----------------------------
INSERT INTO `supplier` VALUES (1, '康师傅食品有限公司', '张经理', '400-123-4567', '天津市西青区', NULL, 5, NULL, 3, 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `supplier` VALUES (2, '统一企业有限公司', '李经理', '400-234-5678', '上海市闵行区', NULL, 4, NULL, 5, 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `supplier` VALUES (3, '娃哈哈集团有限公司', '王经理', '400-345-6789', '杭州市上城区', NULL, 5, NULL, 2, 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `supplier` VALUES (4, '蒙牛乳业有限公司', '赵经理', '400-456-7890', '内蒙古呼和浩特市', NULL, 4, NULL, 4, 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供货商评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier_evaluation
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供货商商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of supplier_product
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'system.name', '个体超市智能管理系统', '系统名称', '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_config` VALUES (2, 'system.version', '1.0.0', '系统版本', '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_config` VALUES (3, 'low.stock.warning', '10', '低库存预警数量', '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_config` VALUES (4, 'high.stock.warning', '1000', '高库存预警数量', '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_config` VALUES (5, 'expire.warning.days', '7', '临期商品预警天数', '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_config` VALUES (6, 'ai.analysis.enabled', 'true', '是否启用AI分析', '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_config` VALUES (7, 'promotion.auto.apply', 'true', '是否自动应用促销活动', '2025-07-16 15:28:03', '2025-07-16 15:28:03');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ADMIN', '系统超级管理员，拥有所有权限', 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_role` VALUES (2, '店长', 'MANAGER', '店长，拥有大部分管理权限', 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_role` VALUES (3, '收银员', 'CASHIER', '收银员，主要负责收银操作', 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1945391279349800963 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$oiaaejS2HqIxLgQ73h7hyuWN0ihdHtPQJILr/v0gAI2ZNousVbC/e', '系统管理员', NULL, NULL, NULL, 1, '2025-07-16 15:28:03', '2025-07-16 15:28:03');
INSERT INTO `sys_user` VALUES (1945391279349800962, 'giao', '$2a$10$fOsUjYbeqYa9..lYvZSuVeyYqT5iHP.TkMb5AObaE9LjpplXrH.1a', '咕噜', '15046049913', '1277623709@qq.com', NULL, 1, '2025-07-16 15:53:19', '2025-07-16 15:53:19');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2025-07-16 15:28:03');

SET FOREIGN_KEY_CHECKS = 1;
