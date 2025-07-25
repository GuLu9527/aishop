# AI服务配置指南

## 问题说明
当前AI对话功能出现401 Unauthorized错误，这是因为DashScope API密钥未正确配置。

## 解决方案

### 1. 获取API密钥
1. 访问阿里云控制台：https://dashscope.console.aliyun.com/
2. 登录您的阿里云账号
3. 在DashScope控制台中创建API密钥
4. 复制生成的API密钥（格式类似：sk-xxxxxxxxxxxxxxxxxx）

### 2. 配置环境变量

#### Windows系统：
```bash
# 临时设置（当前命令行窗口有效）
set AI_DASHSCOPE_API_KEY=sk-your-actual-api-key-here

# 永久设置（推荐）
setx AI_DASHSCOPE_API_KEY "sk-your-actual-api-key-here"
```

#### Linux/Mac系统：
```bash
# 临时设置
export AI_DASHSCOPE_API_KEY=sk-your-actual-api-key-here

# 永久设置（添加到 ~/.bashrc 或 ~/.zshrc）
echo 'export AI_DASHSCOPE_API_KEY=sk-your-actual-api-key-here' >> ~/.bashrc
source ~/.bashrc
```

### 3. 验证配置
设置环境变量后，重启后端服务：
```bash
cd supermarket-backend
mvn spring-boot:run
```

### 4. 测试AI功能
1. 启动前端服务
2. 访问AI对话页面
3. 发送测试消息，如"今天的销售情况如何？"

## 注意事项
- API密钥必须以"sk-"开头
- 请妥善保管您的API密钥，不要泄露给他人
- 如果仍然出现401错误，请检查：
  - API密钥是否正确
  - 阿里云账户是否有足够的余额
  - DashScope服务是否已开通

## 当前配置状态
- ✅ 库存预警功能已集成到库存管理页面
- ❌ AI对话功能需要配置API密钥
- ✅ 前端服务正常运行
- ❌ 后端服务需要解决Java环境配置问题

## 联系支持
如果按照以上步骤仍无法解决问题，请检查：
1. 网络连接是否正常
2. 防火墙设置是否阻止了API调用
3. API密钥是否已过期或被禁用