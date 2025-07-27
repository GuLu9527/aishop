import request from '@/utils/request'

// AI聊天相关接口

/**
 * AI聊天请求接口
 */
export interface AiChatRequest {
  message: string
  conversationId?: string
  userId: number
  userName: string
  messageType?: number  // 1=文本, 2=语音
  createNewConversation?: boolean
}

/**
 * AI聊天响应接口
 */
export interface AiChatResponse {
  sessionId: string
  message: string
  intent?: string
  entities?: any
  action?: string
  actionResult?: any
  suggestions?: string[]
  processTime?: number
  success: boolean
  errorMessage?: string
  messageType?: number
  needConfirmation?: boolean
  confirmationData?: any
}

/**
 * 发送聊天消息
 */
export const chatWithAi = (data: AiChatRequest) => {
  return request({
    url: '/api/ai/chat',
    method: 'post',
    data
  })
}

/**
 * 创建新会话
 */
export const createConversation = (userId: number, title?: string) => {
  return request({
    url: '/api/ai/conversation',
    method: 'post',
    params: { userId, title }
  })
}

/**
 * 获取聊天历史
 */
export const getChatHistory = (conversationId: string, userId: number, limit: number = 50) => {
  return request({
    url: `/api/ai/conversation/${conversationId}/history`,
    method: 'get',
    params: { userId, limit }
  })
}

/**
 * 获取用户会话列表
 */
export const getUserConversations = (userId: number, limit: number = 20) => {
  return request({
    url: '/api/ai/conversations',
    method: 'get',
    params: { userId, limit }
  })
}

/**
 * 结束会话
 */
export const endConversation = (conversationId: string) => {
  return request({
    url: `/api/ai/conversation/${conversationId}/end`,
    method: 'put'
  })
}

/**
 * 删除会话
 */
export const deleteConversation = (conversationId: string, userId: number) => {
  return request({
    url: `/api/ai/conversation/${conversationId}`,
    method: 'delete',
    params: { userId }
  })
}

/**
 * 批量删除所有会话
 */
export const deleteAllConversations = (userId: number) => {
  return request({
    url: '/api/ai/conversations/all',
    method: 'delete',
    params: { userId }
  })
}

/**
 * 获取智能建议
 */
export const getSmartSuggestions = (userId: number) => {
  return request({
    url: '/api/ai/suggestions',
    method: 'get',
    params: { userId }
  })
}

/**
 * 获取快捷操作
 */
export const getQuickActions = () => {
  return request({
    url: '/api/ai/quick-actions',
    method: 'get'
  })
}

/**
 * 流式聊天
 * 使用Fetch Stream进行实时聊天
 */
export const chatStreamWithAi = (data: AiChatRequest, onMessage: (chunk: string) => void, onError?: (error: any) => void, onComplete?: () => void) => {
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  
  // 只发送一个POST请求，使用Fetch Stream接收SSE响应
  fetch(`${baseURL}/api/ai/chat/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    body: JSON.stringify(data)
  }).then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    if (!response.body) {
      throw new Error('Response body is null')
    }
    
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    let currentEvent = { type: '', data: '' }
    
    function readStream(): Promise<void> {
      return reader.read().then(({ done, value }) => {
        if (done) {
          onComplete?.()
          return
        }
        
        // 将新数据添加到缓冲区
        buffer += decoder.decode(value, { stream: true })
        
        // 按行分割处理SSE数据
        const lines = buffer.split('\n')
        // 保留最后一行（可能不完整）
        buffer = lines.pop() || ''
        
        for (const line of lines) {
          const trimmedLine = line.trim()
          
          if (trimmedLine === '') {
            // 空行表示事件结束，处理当前事件
            if (currentEvent.type === 'message' && currentEvent.data) {
              try {
                onMessage(currentEvent.data)
              } catch (e) {
                console.error('Error processing SSE message:', e)
              }
            }
            // 重置当前事件
            currentEvent = { type: '', data: '' }
          } else if (trimmedLine.startsWith('event:')) {
            currentEvent.type = trimmedLine.slice(6).trim()
          } else if (trimmedLine.startsWith('data:')) {
            const eventData = trimmedLine.slice(5).trim()
            if (eventData === '[DONE]') {
              // 流结束信号
              onComplete?.()
              return
            }
            // 如果已有数据，添加换行符
            if (currentEvent.data) {
              currentEvent.data += '\n'
            }
            currentEvent.data += eventData
          } else if (trimmedLine.startsWith(':')) {
            // 忽略注释行
            continue
          }
        }
        
        return readStream()
      })
    }
    
    return readStream()
  }).catch(error => {
    console.error('Stream chat error:', error)
    onError?.(error)
  })
}