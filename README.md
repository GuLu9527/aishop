# AI智能超市管理系统

基于 Spring Boot + Vue3 的新一代智能超市管理系统，集成通义千问等 AI 能力，提供智能化的商品管理、销售分析、库存预警等功能。

## 项目结构

```text
AI_Shop/
├── supermarket-backend/     # 后端服务
│   ├── src/                # 源代码
│   │   ├── main/
│   │   │   ├── java/      # Java代码
│   │   │   └── resources/ # 配置文件
│   │   └── test/          # 测试代码
│   └── pom.xml            # Maven配置
└── supermarket-frontend/    # 前端应用
    ├── src/               # 源代码
    │   ├── api/          # API接口
    │   ├── components/   # 组件
    │   ├── views/        # 页面
    │   ├── router/       # 路由
    │   └── stores/       # 状态管理
    ├── public/           # 静态资源
    └── package.json      # npm配置
```

## 技术栈

### 后端技术栈

- **Spring Boot 3.4.3** - 主框架
- **Spring AI** - AI集成框架 
- **MyBatis Plus** - ORM框架
- **MySQL 8.0** - 数据库
- **Spring Security + JWT** - 安全认证
- **Druid** - 数据库连接池
- **阿里云通义千问** - AI大模型服务
- **Server-Sent Events (SSE)** - 实时流式响应
- **Swagger/OpenAPI 3** - API文档

### 前端技术栈

- **Vue 3 + Composition API** - 前端框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Pinia** - 状态管理
- **Vue Router 4** - 路由管理
- **Element Plus** - UI组件库
- **Fetch API** - HTTP客户端

## 开发进度

### 后端开发进度 (95%)

- [x] 项目基础架构搭建 (100%)
- [x] 数据库设计 (100%)
- [x] 基础配置文件 (100%)
- [x] Spring AI 服务集成 (100%)
- [x] 用户认证与授权 (100%)
- [x] 商品管理模块 (100%)
- [x] 库存管理模块 (95%)
- [x] 销售管理模块 (100%)
- [x] 供应商管理模块 (100%)
- [x] 财务管理模块 (100%) - 真实成本计算优化 ✨
- [x] AI 智能分析功能 (95%) - 销售排行查询优化 ✨
- [x] AI 客服功能 (100%) - 完整重构实现 ✨
- [x] 系统监控与日志 (85%)
- [x] 批量操作优化 (100%) ✨ 新增

### 前端开发进度 (98%)

- [x] 项目架构搭建 (100%)
- [x] 路由配置 (100%)
- [x] API 接口定义 (100%)
- [x] 登录注册页面 (100%)
- [x] 主页仪表盘 (100%)
- [x] 商品管理界面 (100%)
- [x] 库存管理界面 (95%)
- [x] 收银管理界面 (100%)
- [x] 销售分析界面 (95%) - 数据验证增强 ✨
- [x] 供应商管理界面 (100%)
- [x] 财务管理界面 (100%) - 真实财务数据显示 ✨
- [x] AI 客服界面 (100%) - 现代化设计完成 ✨
- [x] 响应式设计优化 (95%) ✨ 新增

## 核心功能

### AI 智能客服系统 ✨ 完整重构

基于通义千问和Spring AI框架的企业级智能客服解决方案：

#### 🤖 核心AI能力
- **意图识别**：自然语言理解，精准识别用户业务需求
- **实体提取**：自动提取关键信息（商品名、时间范围、数量等）
- **流式响应**：Server-Sent Events实现实时打字效果
- **上下文记忆**：维护完整的对话历史和上下文理解

#### 🛠️ 业务工具集成
- **销售数据查询**：支持销售排行、趋势分析、业绩统计
- **库存管理**：实时库存查询、预警检查、补货建议
- **财务概览**：收支统计、利润分析、真实成本计算
- **商品管理**：价格更新、库存调整、商品信息维护

#### 💬 交互体验
- **现代化界面**：Element Plus组件，响应式设计
- **快捷操作**：预设常用业务场景，一键触发
- **会话管理**：支持多会话切换，批量删除优化
- **智能建议**：基于当前对话提供相关问题建议

#### 🔧 技术特性
- **高性能批量操作**：优化的SQL批量删除，避免N+1问题
- **事务保证**：确保数据一致性，避免脏读
- **错误处理**：完善的异常处理和用户提示
- **日志监控**：详细的操作日志和性能监控

### 其他核心功能

#### 📦 商品管理系统
- **商品信息维护**：完整的商品档案，批量操作支持
- **分类管理**：树形分类结构，支持无限级分类
- **价格管理**：实时价格调整，价格历史记录
- **Excel导入导出**：批量商品数据处理

#### 📊 库存管理系统  
- **实时库存监控**：准确的库存数据，多维度查询
- **智能预警**：自定义预警阈值，及时补货提醒
- **库存调整**：支持入库、出库、调拨等操作
- **库存统计**：库存周转率、呆滞商品分析

#### 💰 销售管理系统
- **POS收银**：快速扫码收银，支持多种支付方式
- **销售统计**：实时销售数据，多维度分析报表
- **真实排行**：基于实际销量的商品排行榜 ✨
- **趋势分析**：销售趋势图表，业绩预测

#### 💹 财务管理系统
- **真实成本核算**：基于进货价的真实利润计算 ✨
- **收支记录**：详细的财务流水，自动分类统计
- **利润分析**：商品利润率、毛利润统计
- **财务报表**：多种财务报表，支持导出

## 快速开始

### 环境要求

- **JDK 21** - Java开发环境（推荐使用Oracle JDK或OpenJDK）
- **Maven 3.8+** - 项目构建工具
- **MySQL 8.0+** - 数据库服务
- **Node.js 18+** - 前端运行环境  
- **npm 9+** - 包管理器
- **阿里云通义千问API Key** - AI服务密钥

### 快速启动（推荐）

使用提供的启动脚本快速启动开发环境：

```bash
# Windows 用户
./start_dev.bat

# 或者使用部署脚本进行完整部署
./deploy_ai_customer_service.bat
```

### 手动启动

#### 后端服务启动

1. 克隆项目

```bash
git clone https://github.com/GuLu9527/aishop.git
```

1. 配置环境变量

```bash
cd supermarket-backend
cp .env.example .env
# 编辑 .env 文件，填入实际的配置值
```

1. 创建数据库

```sql
CREATE DATABASE supermarket_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

1. 运行项目

```bash
mvn spring-boot:run
```

### 前端项目启动

1. 安装依赖

```bash
cd supermarket-frontend
npm install
```

1. 启动开发服务器

```bash
npm run dev
```

### 访问系统

启动成功后，可以通过以下地址访问系统：

- **前端主页**：http://localhost:5173
- **AI智能客服**：http://localhost:5173/ai-chat ✨
- **后端API文档**：http://localhost:8080/swagger-ui/index.html
- **后端API**：http://localhost:8080

默认管理员账号：
- 用户名：`admin`
- 密码：`123456`

### 功能入口

| 功能模块 | 访问路径 | 说明 |
|---------|---------|------|
| 仪表盘 | `/dashboard` | 数据概览和统计图表 |
| AI客服 | `/ai-chat` | 智能客服对话界面 ✨ |
| 商品管理 | `/products` | 商品信息管理 |
| 库存管理 | `/inventory` | 库存监控和调整 |
| 收银台 | `/cashier` | POS收银系统 |
| 销售分析 | `/sales` | 销售数据分析 |
| 财务管理 | `/finance` | 财务报表和统计 |
| 供应商管理 | `/suppliers` | 供应商信息管理 |

## 配置说明

### 关键配置项

系统使用YAML配置文件进行管理，主要配置包括：

#### 数据库配置 (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/supermarket_db
    username: root
    password: your-password
```
```yaml
spring:
  ai:
    dashscope:
      api-key: your-tongyi-api-key
      chat:
        options:
          model: qwen-plus
          temperature: 0.7
```

#### 其他重要配置
- **JWT认证配置**：令牌过期时间、密钥等
- **文件上传配置**：支持的文件类型、大小限制
- **日志配置**：日志级别、输出格式

配置文件位置：
- `supermarket-backend/src/main/resources/application.yml` - 主配置

## 最新更新 ✨

### v2.1.0 (2025-01-27)

#### 🔥 重大更新
- **AI客服系统完整重构**：基于Spring AI框架实现企业级智能客服
- **真实财务数据计算**：基于实际成本的利润计算，告别假数据
- **销售排行优化**：修复查询逻辑，支持真实的商品销量排行
- **批量操作性能优化**：SQL批量操作，避免N+1查询问题

#### 🛠️ 技术改进
- 升级到**Spring Boot 3.4.3** + **Spring AI**
- **Server-Sent Events**流式响应
- **批量删除优化**：单次API调用替代多次请求
- **UI现代化改造**：Element Plus组件，响应式设计

#### 🐛 问题修复
- 修复AI消息显示位置问题
- 修复删除聊天记录后刷新页面仍显示的问题
- 修复销售排行查询逻辑错误
- 修复财务模块固定利润率问题

## 参与贡献

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 许可证

[MIT](LICENSE)

## 联系方式

- 项目负责人：GuLu9527
- GitHub：[https://github.com/GuLu9527/aishop](https://github.com/GuLu9527/aishop)
