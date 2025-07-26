# AI客服功能模块

## 功能概述

AI客服功能模块为超市管理系统提供智能客服服务，支持自然语言对话、知识库查询、工单管理等功能。

## 主要特性

### 1. 智能对话
- 支持自然语言理解和响应
- 集成阿里云通义千问AI服务
- 智能意图识别和实体提取
- 上下文感知的多轮对话

### 2. 知识库管理
- 可配置的知识库内容
- 关键词匹配和语义搜索
- 知识点命中统计和效果评估
- 支持分类管理和优先级设置

### 3. 客服会话管理
- 会话状态跟踪
- 人工客服转接
- 客户满意度评价
- 会话历史记录

### 4. 工单系统
- 自动工单创建
- 工单分配和处理
- 工单状态跟踪
- 问题解决方案记录

## 数据库表结构

### ai_customer_session (AI客服会话表)
- 管理客服会话的生命周期
- 记录客户信息和会话状态
- 支持人工客服转接

### ai_knowledge_base (AI知识库表)
- 存储客服知识库内容
- 支持关键词和意图匹配
- 记录知识点使用统计

### ai_service_evaluation (AI客服评价表)
- 收集客户服务评价
- 多维度评分体系
- 服务质量分析

### ai_service_ticket (AI客服工单表)
- 管理客服工单流程
- 支持优先级和状态管理
- 工单分配和解决跟踪

## API接口说明

### 客服对话接口
```
POST /api/ai/customer/chat
- 发送客户消息，获取AI回复

GET /api/ai/customer/chat/stream
- 流式对话接口，实时返回AI回复

GET /api/ai/customer/session/{sessionId}
- 获取会话详情

GET /api/ai/customer/session/{sessionId}/history
- 获取会话历史消息
```

### 知识库接口
```
GET /api/ai/customer/knowledge/search
- 搜索知识库内容

GET /api/ai/customer/knowledge/intent/{intent}
- 根据意图获取知识库内容
```

### 评价和工单接口
```
POST /api/ai/customer/evaluation
- 提交服务评价

POST /api/ai/customer/ticket
- 创建服务工单

GET /api/ai/customer/tickets
- 获取客户工单列表
```

## 部署步骤

### 1. 数据库初始化
```sql
-- 执行SQL脚本创建表结构
source ai_customer_service_tables.sql
```

### 2. 配置AI服务
在 `application.yml` 中配置阿里云AI服务：
```yaml
ai:
  tongyi:
    api-key: your-api-key
    model: qwen-turbo
    max-tokens: 2000
```

### 3. 启动应用
```bash
mvn spring-boot:run
```

### 4. 初始化知识库
系统启动后会自动插入基础知识库数据，也可以通过管理界面添加更多知识内容。

## 使用说明

### 1. 客户端集成
前端可以通过以下方式集成AI客服：

```javascript
// 发送消息
const response = await axios.post('/api/ai/customer/chat', {
  sessionId: 'session-123',
  customerId: 1,
  customerName: '张三',
  content: '我想查询商品库存',
  messageType: 1
});

// 流式对话
const eventSource = new EventSource('/api/ai/customer/chat/stream?message=你好');
eventSource.onmessage = function(event) {
  const data = JSON.parse(event.data);
  console.log(data.content);
};
```

### 2. 知识库管理
管理员可以通过后台管理界面：
- 添加新的知识库内容
- 设置关键词和意图映射
- 查看知识点使用统计
- 优化知识库内容

### 3. 工单处理
客服人员可以：
- 查看待处理工单
- 分配工单给相应人员
- 记录问题解决方案
- 跟踪工单处理进度

## 配置说明

### AI服务配置
```yaml
ai:
  customer-service:
    enabled: true
    max-session-duration: 3600  # 会话最大持续时间（秒）
    auto-transfer-threshold: 3  # 自动转人工阈值
    knowledge-base-cache: true  # 是否缓存知识库
```

### 知识库配置
- 支持JSON格式的关键词配置
- 可设置知识点优先级
- 支持分类管理
- 自动统计命中次数

## 监控和日志

### 性能监控
- AI服务调用次数和响应时间
- 知识库命中率统计
- 客户满意度趋势分析

### 日志记录
- 所有AI对话记录
- 知识库查询日志
- 工单处理日志
- 系统错误日志

## 常见问题

### Q: AI回复不准确怎么办？
A: 可以通过以下方式优化：
1. 完善知识库内容
2. 调整关键词匹配规则
3. 优化意图识别配置
4. 收集用户反馈进行改进

### Q: 如何提高客户满意度？
A: 建议：
1. 定期分析客户评价数据
2. 优化常见问题的回复模板
3. 及时处理需要人工介入的问题
4. 持续更新知识库内容

### Q: 系统性能如何优化？
A: 可以考虑：
1. 启用知识库缓存
2. 优化数据库查询
3. 使用连接池管理AI服务调用
4. 实施负载均衡

## 技术支持

如有技术问题，请联系开发团队或查看详细的API文档。