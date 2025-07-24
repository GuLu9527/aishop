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
  conversationId: string
  reply: string
  intent?: string
  entities?: any
  operationResult?: any
  suggestedQuestions?: string[]
  processingTime?: number
  success: boolean
  errorMessage?: string
  messageType?: string
  needConfirmation?: boolean
  confirmationParams?: any
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
export const getChatHistory = (conversationId: string, limit: number = 50) => {
  return request({
    url: `/api/ai/conversation/${conversationId}/history`,
    method: 'get',
    params: { limit }
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
export const deleteConversation = (conversationId: string) => {
  return request({
    url: `/api/ai/conversation/${conversationId}`,
    method: 'delete'
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