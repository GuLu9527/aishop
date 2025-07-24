# AI智能超市管理系统

基于 Spring Boot + Vue3 的新一代智能超市管理系统，集成通义千问等 AI 能力，提供智能化的商品管理、销售分析、库存预警等功能。

## 项目结构

```text
AI_Shop/
├── docs/                    # 项目文档
├── supermarket-backend/     # 后端服务
└── supermarket-frontend/    # 前端应用
```

## 技术栈

### 后端技术栈

- Spring Boot
- MyBatis Plus
- MySQL
- Redis (计划中)
- Spring Security + JWT
- Druid
- 通义千问 API

### 前端技术栈

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Element Plus

## 开发进度

### 后端开发进度 (30%)

- [x] 项目基础架构搭建
- [x] 数据库设计
- [x] 基础配置文件
- [x] AI 服务集成
- [ ] 用户认证与授权
- [ ] 商品管理模块
- [ ] 库存管理模块
- [ ] 销售管理模块
- [ ] 供应商管理模块
- [ ] 财务管理模块
- [ ] AI 智能分析功能
- [ ] 系统监控与日志

### 前端开发进度 (20%)

- [x] 项目架构搭建
- [x] 路由配置
- [x] API 接口定义
- [ ] 登录注册页面
- [ ] 主页仪表盘
- [ ] 商品管理界面
- [ ] 库存管理界面
- [ ] 销售管理界面
- [ ] 供应商管理界面
- [ ] 财务报表界面
- [ ] AI 助手交互界面

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+
- npm 9+

### 后端服务启动

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

## 配置说明

系统使用环境变量进行配置管理，主要配置项包括：

- 数据库连接配置
- AI 服务配置
- JWT 认证配置
- 阿里云服务配置
- 其他系统配置

详细配置说明请参考 `supermarket-backend/.env.example` 文件。

## 项目文档

更多详细文档请查看 `docs` 目录：

- [系统方案设计](docs/个体超市智能管理系统方案（更新版）.md)
- [开发任务文档](docs/个体超市智能管理系统开发任务文档.md)
- [环境准备清单](docs/开发环境准备清单.md)
- [AI界面设计](docs/零学习成本AI界面设计.md)
- [AI架构设计](docs/AI架构设计与应用场景分析.md)

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
