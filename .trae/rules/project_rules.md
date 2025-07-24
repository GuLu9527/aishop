# 个体超市智能管理系统 - 前端开发提示词

## 项目背景
你正在开发一个个体超市智能管理系统的前端部分，这是一个面向小型超市老板的管理工具，需要简洁易用、功能完整、你最喜欢的风格是ios。

## 技术栈要求
- **框架**: Vue 3 + Composition API
- **UI库**: Element Plus
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **图表库**: ECharts
- **样式**: SCSS + Element Plus主题定制

## 核心功能模块

### 1. 用户认证模块
- 登录页面（支持管理员、收银员角色）
- 权限路由守卫
- Token自动刷新机制
- 用户信息展示和退出

### 2. 库存管理模块
- 商品信息管理（CRUD操作）
- 库存监控仪表板（实时数据展示）
- 库存预警提醒（低库存、临期商品）
- 智能补货建议展示
- 库存变动记录查询
- 商品条码扫描功能

### 3. 销售分析模块
- 销售数据仪表板（图表展示）
- 销售趋势分析（折线图、柱状图）
- 商品销售排行榜
- 销售报表生成和导出
- 实时销售数据监控

### 4. 智能收银模块
- 收银台界面（商品扫码、计算总价）
- 支付方式选择（现金、微信、支付宝）
- 收银记录查询
- 小票打印功能
- 自助收银界面（简化版）

### 5. 供货商管理模块
- 供货商信息管理
- 供货商商品关联管理
- 采购订单管理
- 供货商评价功能
- 供货商对账页面

### 6. 活动促销模块
- 促销活动配置（满减、折扣、买赠等）
- 促销商品管理
- 促销效果分析
- 促销信息发布
- 优惠券管理

### 7. 财务管理模块
- 财务概览仪表板
- 收支记录管理
- 利润分析页面
- 财务报表展示
- 数据导出功能

### 8. AI智能助手模块
- AI对话聊天界面
- 消息发送和接收功能
- 快捷操作按钮
- 对话历史记录展示
- AI助手悬浮窗组件
- 意图配置管理界面（管理员）

## 开发规范

### 代码规范
```javascript
// 1. 使用Composition API
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 2. 组件命名使用PascalCase
// 文件名: ProductManagement.vue
export default {
  name: 'ProductManagement'
}

// 3. 响应式数据定义
const formData = reactive({
  name: '',
  price: 0,
  stock: 0
})

const loading = ref(false)
const tableData = ref([])

// 4. 计算属性
const filteredData = computed(() => {
  return tableData.value.filter(item => item.status === 'active')
})
```

### UI设计原则
- **简洁明了**: 界面布局清晰，避免复杂操作
- **响应式设计**: 适配不同屏幕尺寸
- **一致性**: 统一的颜色、字体、间距规范
- **易用性**: 符合超市老板的使用习惯
- **反馈及时**: 操作后及时给出反馈提示

### 组件设计规范
```vue
<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ pageTitle }}</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加商品
      </el-button>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-container">
      <el-form :model="searchForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="price" label="价格" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>
```

### 状态管理规范
```javascript
// stores/product.js
import { defineStore } from 'pinia'
import { productApi } from '@/api/product'

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    loading: false,
    currentProduct: null
  }),

  getters: {
    lowStockProducts: (state) => {
      return state.products.filter(product => product.stock < product.minStock)
    }
  },

  actions: {
    async fetchProducts() {
      this.loading = true
      try {
        const response = await productApi.getList()
        this.products = response.data
      } catch (error) {
        console.error('获取商品列表失败:', error)
      } finally {
        this.loading = false
      }
    }
  }
})
```

### API调用规范
```javascript
// api/product.js
import request from '@/utils/request'

export const productApi = {
  // 获取商品列表
  getList(params) {
    return request({
      url: '/api/products',
      method: 'get',
      params
    })
  },

  // 创建商品
  create(data) {
    return request({
      url: '/api/products',
      method: 'post',
      data
    })
  },

  // 更新商品
  update(id, data) {
    return request({
      url: `/api/products/${id}`,
      method: 'put',
      data
    })
  },

  // 删除商品
  delete(id) {
    return request({
      url: `/api/products/${id}`,
      method: 'delete'
    })
  }
}
```

## 关键开发要点

### 1. 数据可视化
- 使用ECharts展示销售趋势、库存状态等
- 图表要清晰易读，支持数据钻取
- 实时数据更新，使用WebSocket或定时轮询

### 2. 用户体验优化
- 加载状态提示（Loading、Skeleton）
- 操作确认对话框
- 表单验证和错误提示
- 快捷键支持（如收银台的数字键盘）

### 3. 移动端适配
- 响应式布局设计
- 触摸友好的交互设计
- 移动端专用的收银界面

### 4. 性能优化
- 组件懒加载
- 图片懒加载
- 虚拟滚动（大数据量表格）
- 防抖和节流处理

### 5. 错误处理
- 全局错误拦截
- 网络异常处理
- 用户友好的错误提示

## 开发流程建议

1. **搭建项目基础架构**（路由、状态管理、API封装）
2. **开发通用组件**（表格、表单、图表等）
3. **实现用户认证模块**
4. **按业务模块逐步开发**（库存→销售→收银→其他）
5. **集成测试和优化**
6. **移动端适配**

## 注意事项

- 考虑超市老板的使用习惯，界面要直观易懂
- 重要操作要有确认提示
- 数据展示要准确及时
- 支持离线使用的基本功能
- 做好数据备份和恢复机制
- 考虑多用户同时操作的并发问题

请根据这个提示词进行前端开发，确保代码质量和用户体验。
# 个体超市智能管理系统 - 后端开发提示词

## 项目背景
你正在开发一个个体超市智能管理系统的后端服务，需要提供稳定可靠的API接口，支持库存管理、销售分析、收银结算等核心业务功能，并集成阿里云AI服务。

## 技术栈要求
- **框架**: Spring Boot 3.x
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0 + MyBatis Plus
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **AI服务**: 阿里云PAI + 通义千问API
- **支付**: 支付宝SDK + 微信支付SDK
- **文档**: Swagger/OpenAPI 3
- **监控**: Spring Boot Actuator

## 核心业务模块

### 1. 用户认证与权限管理
- JWT Token认证机制
- 角色权限管理（RBAC）
- 用户登录/注册/注销
- Token刷新和过期处理
- 密码加密存储（BCrypt）

### 2. 库存管理模块
- 商品信息CRUD操作
- 库存实时查询和更新
- 库存变动记录追踪
- 库存预警机制（低库存、临期商品）
- 阿里云PAI智能补货预测集成
- 库存盘点功能

### 3. 销售分析模块
- 销售数据统计和聚合
- 阿里云PAI销售趋势分析集成
- 通义千问商品关联分析集成
- 销售报表生成
- 实时销售数据推送

### 4. 智能收银模块
- 收银结算业务逻辑
- 支付宝/微信支付集成
- 支付回调处理
- 收银记录管理
- 退款处理机制

### 5. 供货商管理模块
- 供货商信息管理
- 供货商商品关联
- 采购订单管理
- 供货商评价系统
- 对账功能

### 6. 活动促销模块
- 促销规则引擎
- 促销活动配置
- 促销效果统计
- 优惠券管理
- 促销信息推送

### 7. 财务管理模块
- 收支记录管理
- 利润核算
- 财务报表生成
- 成本分析
- 数据导出功能

### 8. AI智能助手模块
- AI对话会话管理
- 意图识别和实体提取
- API智能调用引擎
- 自然语言理解服务
- 智能响应生成
- AI操作日志记录

## 开发规范

### 项目结构
```
src/main/java/com/supermarket/
├── config/          # 配置类
├── controller/      # 控制器层
├── service/         # 业务逻辑层
├── mapper/          # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── vo/              # 视图对象
├── enums/           # 枚举类
├── exception/       # 异常处理
├── utils/           # 工具类
├── security/        # 安全配置
└── ai/              # AI服务集成
```

### 统一响应格式
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, System.currentTimeMillis());
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis());
    }
}
```

### 控制器层规范
```java
@RestController
@RequestMapping("/api/products")
@Api(tags = "商品管理")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @ApiOperation("获取商品列表")
    public Result<PageResult<ProductVO>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            ProductQueryDTO queryDTO) {
        
        PageResult<ProductVO> result = productService.getProducts(page, size, queryDTO);
        return Result.success(result);
    }

    @PostMapping
    @ApiOperation("创建商品")
    public Result<ProductVO> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductVO product = productService.createProduct(createDTO);
        return Result.success(product);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新商品")
    public Result<ProductVO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        
        ProductVO product = productService.updateProduct(id, updateDTO);
        return Result.success(product);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除商品")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success(null);
    }
}
```

### 服务层规范
```java
@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private AliCloudAIService aliCloudAIService;

    @Override
    public PageResult<ProductVO> getProducts(Integer page, Integer size, ProductQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getName()), Product::getName, queryDTO.getName())
               .eq(queryDTO.getCategoryId() != null, Product::getCategoryId, queryDTO.getCategoryId())
               .ge(queryDTO.getMinPrice() != null, Product::getPrice, queryDTO.getMinPrice())
               .le(queryDTO.getMaxPrice() != null, Product::getPrice, queryDTO.getMaxPrice());

        // 分页查询
        Page<Product> pageInfo = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(pageInfo, wrapper);

        // 转换为VO
        List<ProductVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public ProductVO createProduct(ProductCreateDTO createDTO) {
        // 参数验证
        validateProductData(createDTO);

        // 转换为实体
        Product product = convertToEntity(createDTO);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        // 保存到数据库
        productMapper.insert(product);

        // 清除相关缓存
        clearProductCache();

        // 异步调用AI服务进行商品分析
        CompletableFuture.runAsync(() -> {
            try {
                aliCloudAIService.analyzeNewProduct(product);
            } catch (Exception e) {
                log.error("AI商品分析失败: {}", e.getMessage());
            }
        });

        return convertToVO(product);
    }
}
```

### 数据库设计规范
```sql
-- 商品表
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `barcode` varchar(50) UNIQUE COMMENT '商品条码',
  `category_id` bigint COMMENT '分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `cost` decimal(10,2) COMMENT '成本价',
  `stock` int DEFAULT 0 COMMENT '库存数量',
  `min_stock` int DEFAULT 0 COMMENT '最低库存',
  `unit` varchar(20) COMMENT '单位',
  `description` text COMMENT '商品描述',
  `image_url` varchar(500) COMMENT '商品图片',
  `status` tinyint DEFAULT 1 COMMENT '状态：1-正常，0-停用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_barcode` (`barcode`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

## 阿里云AI服务集成

### PAI智能补货预测
```java
@Service
@Slf4j
public class AliCloudPAIService {

    @Value("${alicloud.pai.endpoint}")
    private String paiEndpoint;
    
    @Value("${alicloud.pai.access-key}")
    private String accessKey;

    public ReplenishmentPrediction predictReplenishment(Long productId) {
        try {
            // 获取历史销售数据
            List<SalesData> salesHistory = getSalesHistory(productId);
            
            // 构建PAI请求参数
            PAIRequest request = PAIRequest.builder()
                    .productId(productId)
                    .salesHistory(salesHistory)
                    .seasonalFactors(getSeasonalFactors())
                    .promotionData(getPromotionData(productId))
                    .build();

            // 调用PAI服务
            PAIResponse response = paiClient.predict(request);
            
            // 解析预测结果
            return parseReplenishmentResult(response);
            
        } catch (Exception e) {
            log.error("PAI补货预测失败: {}", e.getMessage());
            throw new BusinessException("智能补货预测服务异常");
        }
    }
}
```

### 通义千问API集成
```java
@Service
@Slf4j
public class TongYiQianWenService {

    @Value("${alicloud.tongyi.api-key}")
    private String apiKey;

    public AssociationAnalysisResult analyzeProductAssociation(List<SalesRecord> salesData) {
        try {
            // 构建提示词
            String prompt = buildAssociationAnalysisPrompt(salesData);
            
            // 调用通义千问API
            TongYiRequest request = TongYiRequest.builder()
                    .model("qwen-plus")
                    .messages(Arrays.asList(
                        Message.builder()
                            .role("user")
                            .content(prompt)
                            .build()
                    ))
                    .temperature(0.3)
                    .build();

            TongYiResponse response = tongYiClient.chat(request);
            
            // 解析AI分析结果
            return parseAssociationResult(response.getChoices().get(0).getMessage().getContent());
            
        } catch (Exception e) {
            log.error("商品关联分析失败: {}", e.getMessage());
            throw new BusinessException("AI分析服务异常");
        }
    }

    private String buildAssociationAnalysisPrompt(List<SalesRecord> salesData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下超市销售数据，找出商品之间的关联购买规律：\n\n");
        
        // 添加销售数据
        salesData.forEach(record -> {
            prompt.append(String.format("订单ID: %s, 商品: %s, 数量: %d, 时间: %s\n", 
                record.getOrderId(), record.getProductNames(), 
                record.getQuantity(), record.getSaleTime()));
        });
        
        prompt.append("\n请以JSON格式返回分析结果，包含：\n");
        prompt.append("1. 强关联商品对（支持度>0.3，置信度>0.6）\n");
        prompt.append("2. 推荐的商品陈列建议\n");
        prompt.append("3. 促销搭配建议\n");
        
        return prompt.toString();
    }
}
```

## 关键开发要点

### 1. 数据一致性
- 使用事务确保数据一致性
- 分布式锁处理并发问题
- 数据库连接池优化

### 2. 缓存策略
- Redis缓存热点数据
- 缓存更新策略（写入时失效）
- 缓存穿透和雪崩防护

### 3. 异步处理
- 使用RabbitMQ处理耗时操作
- AI服务调用异步化
- 消息重试机制

### 4. 安全防护
- SQL注入防护
- XSS攻击防护
- 接口限流和防刷
- 敏感数据加密

### 5. 监控和日志
- 接口性能监控
- 业务日志记录
- 异常告警机制
- 健康检查接口

## 部署和运维

### 配置管理
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/supermarket?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    
alicloud:
  pai:
    endpoint: ${PAI_ENDPOINT}
    access-key: ${PAI_ACCESS_KEY}
    secret-key: ${PAI_SECRET_KEY}
  tongyi:
    api-key: ${TONGYI_API_KEY}
```

### Docker部署
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/supermarket-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

请根据这个提示词进行后端开发，确保代码质量和系统稳定性。
