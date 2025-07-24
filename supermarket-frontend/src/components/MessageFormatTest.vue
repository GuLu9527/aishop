<template>
  <div class="test-container">
    <h2>AI消息格式测试</h2>
    
    <div class="test-section">
      <h3>测试内容</h3>
      <textarea 
        v-model="testContent" 
        rows="10" 
        cols="50"
        placeholder="输入测试内容..."
      ></textarea>
      <br>
      <el-button @click="updateTest" type="primary">更新测试</el-button>
    </div>
    
    <div class="test-section">
      <h3>格式化结果</h3>
      <AiMessage :content="testContent" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AiMessage from './AiMessage.vue'

const testContent = ref(`## 📊 销售数据分析

根据您的查询，为您整理了以下销售数据：

### 📈 核心指标
- **今日销售额**: ¥12,345
- **订单数量**: 156单
- **平均客单价**: ¥79.2

### 数据趋势
1. 销售额比昨日增长 **+10.2%**
2. 订单量增加了 **14单**
3. 客单价略有下降 **-2.1%**

### 💡 分析建议
- 销售表现良好，建议继续保持
- 可以考虑推广热销商品
- 关注库存补充情况

这是一个普通段落，用来测试段落格式化效果。`)

const updateTest = () => {
  console.log('更新测试内容')
}
</script>

<style scoped>
.test-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

textarea {
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-family: monospace;
}
</style>
