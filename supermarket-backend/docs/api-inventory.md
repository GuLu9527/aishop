# 超市管理系统 - 后端接口清单

## 📋 接口分类概览

### 🔍 查询类接口 (Query APIs)
> 只读操作，获取数据，安全级别：低

#### 1. 商品管理 (`/api/products`)
- `POST /api/products/page` - 分页查询商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/search` - 搜索商品
- `GET /api/products/barcode/{barcode}` - 根据条码查询商品

#### 2. 库存管理 (`/api/inventory`)
- `GET /api/inventory/page` - 分页查询库存列表
- `GET /api/inventory/{productId}` - 获取商品库存详情
- `GET /api/inventory/alerts` - 获取库存预警列表
- `GET /api/inventory/statistics` - 获取库存统计数据

#### 3. 供货商管理 (`/api/suppliers`)
- `POST /api/suppliers/page` - 分页查询供货商列表
- `GET /api/suppliers/{id}` - 获取供货商详情
- `GET /api/suppliers/all` - 获取所有供货商

#### 4. 商品分类 (`/api/categories`)
- `GET /api/categories` - 查询所有启用的分类
- `GET /api/categories/tree` - 获取分类树结构

#### 5. 收银管理 (`/api/cashier`)
- `GET /api/cashier/held-transactions` - 获取挂单列表
- `GET /api/cashier/sales-summary` - 获取销售汇总

#### 6. 系统配置 (`/api/sys-config`)
- `GET /api/sys-config/value/{configKey}` - 获取配置值
- `GET /api/sys-config/all` - 获取所有配置

#### 7. AI助手 (`/api/ai`)
- `GET /api/ai/conversations` - 获取对话历史
- `GET /api/ai/capabilities` - 获取AI能力描述
- `GET /api/ai/tools` - 获取可用工具列表

### ⚡ 操作类接口 (Operation APIs)
> 修改数据，业务操作，安全级别：中

#### 1. 商品管理
- `POST /api/products` - 新增商品
- `PUT /api/products/{id}` - 更新商品信息
- `POST /api/products/batch` - 批量操作商品

#### 2. 库存管理
- `POST /api/inventory/adjust` - 库存调整
- `POST /api/inventory/in` - 入库操作
- `POST /api/inventory/out` - 出库操作

#### 3. 供货商管理
- `POST /api/suppliers` - 新增供货商
- `PUT /api/suppliers/{id}` - 更新供货商信息

#### 4. 收银管理
- `POST /api/cashier/scan` - 扫码商品
- `POST /api/cashier/payment` - 处理支付
- `POST /api/cashier/hold` - 保存挂单
- `POST /api/cashier/resume/{transactionId}` - 恢复挂单

#### 5. AI助手
- `POST /api/ai/chat` - AI聊天对话 (流式传输)
- `POST /api/ai/stream` - AI流式聊天

### 🔒 管理类接口 (Management APIs)
> 系统管理，高权限操作，安全级别：高

#### 1. 认证管理 (`/api/auth`)
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/refresh` - 刷新Token

#### 2. 商品管理
- `DELETE /api/products/{id}` - 删除商品
- `POST /api/products/import` - 导入商品数据
- `GET /api/products/export` - 导出商品数据

#### 3. 供货商管理
- `DELETE /api/suppliers/{id}` - 删除供货商
- `GET /api/suppliers/export` - 导出供货商数据

#### 4. 系统配置
- `PUT /api/sys-config` - 更新系统配置
- `DELETE /api/sys-config/{configKey}` - 删除配置项

#### 5. AI助手
- `DELETE /api/ai/conversations/{sessionId}` - 删除对话
- `DELETE /api/ai/conversations/clear` - 清空所有对话

### 🧪 测试类接口 (Test APIs)
> 开发测试用，生产环境可禁用

#### 测试接口 (`/api/test`)
- `GET /api/test/ping` - 连通性测试
- `POST /api/test/echo` - 回声测试
- `DELETE /api/test/conversations/{sessionId}` - 测试删除请求
- `GET /api/test/conversations` - 测试获取请求

## 🛡️ 安全级别定义

### 🟢 低风险 (查询类)
- **特征**: 只读操作，不修改数据
- **权限**: 基础用户权限即可
- **适合AI调用**: ✅ 是
- **示例**: 查询商品、查看库存、获取统计数据

### 🟡 中风险 (操作类)
- **特征**: 修改业务数据，但不影响系统安全
- **权限**: 需要业务操作权限
- **适合AI调用**: ⚠️ 需要用户确认
- **示例**: 添加商品、库存调整、收银操作

### 🔴 高风险 (管理类)
- **特征**: 系统管理，删除数据，配置修改
- **权限**: 需要管理员权限
- **适合AI调用**: ❌ 不建议
- **示例**: 删除数据、系统配置、用户管理

## 📊 接口统计

| 分类 | 数量 | AI可调用 | 备注 |
|------|------|----------|------|
| 查询类 | 20+ | ✅ 全部 | 安全，推荐AI调用 |
| 操作类 | 15+ | ⚠️ 部分 | 需要用户确认 |
| 管理类 | 10+ | ❌ 禁止 | 高风险，不建议 |
| 测试类 | 4 | ✅ 可以 | 仅开发环境 |

## 🎯 AI工具调用建议

### 推荐AI调用的接口
1. **商品查询**: 搜索商品、获取商品信息
2. **库存查询**: 查看库存状态、预警信息
3. **统计数据**: 销售统计、库存统计
4. **基础信息**: 供货商信息、分类信息

### 需要用户确认的接口
1. **库存调整**: 入库、出库、调整操作
2. **商品管理**: 添加、修改商品信息
3. **收银操作**: 扫码、支付处理

### 禁止AI调用的接口
1. **删除操作**: 删除商品、供货商等
2. **系统配置**: 修改系统设置
3. **用户管理**: 登录、权限管理

---

*最后更新: 2025-01-22*
*版本: v1.0*
