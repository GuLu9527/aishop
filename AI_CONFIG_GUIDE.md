# AI对话功能配置指南

## 概述
本系统的AI对话功能基于阿里云通义千问API实现，需要配置相应的环境变量才能正常使用。

## 必需配置的环境变量

### 1. DASHSCOPE_API_KEY
- **描述**: 阿里云DashScope API密钥
- **获取方式**: 
  1. 登录阿里云控制台
  2. 进入DashScope服务
  3. 创建API Key
- **配置方式**: 
  ```bash
  # Windows (PowerShell)
  $env:DASHSCOPE_API_KEY="your_api_key_here"
  
  # Windows (CMD)
  set DASHSCOPE_API_KEY=your_api_key_here
  
  # Linux/Mac
  export DASHSCOPE_API_KEY=your_api_key_here
  ```

## 可选配置参数

### 2. AI模型配置
在 `application.yml` 中可以配置以下参数：

```yaml
spring:
  ai:
    alibaba:
      dashscope:
        api-key: ${DASHSCOPE_API_KEY:your_api_key_here}
        model: ${AI_MODEL:qwen-turbo}
        temperature: ${AI_TEMPERATURE:0.7}
        max-tokens: ${AI_MAX_TOKENS:2000}

alicloud:
  tongyi:
    api-key: ${DASHSCOPE_API_KEY:your_api_key_here}
    model: ${AI_MODEL:qwen-turbo}
    temperature: ${AI_TEMPERATURE:0.7}
    max-tokens: ${AI_MAX_TOKENS:2000}
```

### 环境变量说明：

- **AI_MODEL** (可选)
  - 默认值: `qwen-turbo`
  - 可选值: `qwen-turbo`, `qwen-plus`, `qwen-max`
  - 说明: 选择使用的通义千问模型

- **AI_TEMPERATURE** (可选)
  - 默认值: `0.7`
  - 取值范围: 0.0 - 2.0
  - 说明: 控制AI回复的随机性，值越高越随机

- **AI_MAX_TOKENS** (可选)
  - 默认值: `2000`
  - 取值范围: 1 - 8000
  - 说明: AI回复的最大token数量

## 配置步骤

### 1. 获取API密钥
1. 访问 [阿里云DashScope控制台](https://dashscope.console.aliyun.com/)
2. 登录您的阿里云账号
3. 在API-KEY管理页面创建新的API Key
4. 复制生成的API Key

### 2. 设置环境变量
根据您的操作系统选择合适的方式设置环境变量：

#### Windows PowerShell:
```powershell
$env:DASHSCOPE_API_KEY="sk-xxxxxxxxxxxxxxxxxx"
```

#### Windows CMD:
```cmd
set DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxxxxxxx
```

#### Linux/Mac:
```bash
export DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxxxxxxx
```

### 3. 重启应用
设置环境变量后，需要重启Spring Boot应用以使配置生效。

## 验证配置

### 1. 检查配置状态
应用启动后，可以通过以下方式检查AI配置是否正确：

```bash
# 查看应用日志，确认AI服务初始化成功
# 或者访问AI聊天页面测试功能
```

### 2. 测试AI对话
1. 启动前端和后端服务
2. 访问AI聊天页面
3. 发送测试消息
4. 确认能够收到AI回复

## 常见问题

### 1. API Key无效
- 检查API Key是否正确复制
- 确认API Key是否已激活
- 检查阿里云账户余额

### 2. 网络连接问题
- 确认服务器能够访问外网
- 检查防火墙设置
- 确认代理配置（如有）

### 3. 配置未生效
- 确认环境变量设置正确
- 重启应用服务
- 检查application.yml配置

## 安全注意事项

1. **不要将API Key硬编码在代码中**
2. **不要将API Key提交到版本控制系统**
3. **定期轮换API Key**
4. **限制API Key的使用权限**
5. **监控API使用量和费用**

## 费用说明

通义千问API按调用次数和token数量计费，请注意：
- 合理设置max-tokens参数
- 监控API调用频率
- 定期检查费用账单

## 技术支持

如遇到配置问题，可以：
1. 查看应用日志获取详细错误信息
2. 参考阿里云DashScope官方文档
3. 联系技术支持团队