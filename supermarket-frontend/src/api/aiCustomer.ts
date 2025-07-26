import request from '@/utils/request'
import { aiGet, aiPost } from '@/utils/aiRequest'
import type { AxiosResponse } from 'axios'

// ==================== 接口类型定义 ====================

// AI客服聊天请求
export interface AiCustomerChatRequest {
  sessionId?: string
  customerId: number
  customerName: string
  message: string
  messageType?: number
  customerContact?: string
}

// AI客服聊天响应
export interface AiCustomerChatResponse {
  sessionId: string
  messageId: string
  content: string
  intent?: string
  needHumanIntervention: boolean
  suggestedActions?: string[]
  knowledgeBaseId?: number
  processingTime: number
  createTime: string
}

// AI客服会话信息
export interface AiCustomerSessionVO {
  sessionId: string
  customerId: number
  customerName: string
  customerPhone?: string
  sessionTitle: string
  status: number
  messageCount: number
  lastMessageContent?: string
  lastMessageTime?: string
  needHumanIntervention: boolean
  assignedStaffId?: number
  assignedStaffName?: string
  customerSatisfaction?: number
  createTime: string
  updateTime: string
}

// 消息记录
export interface AiMessageVO {
  messageId: string
  sessionId: string
  messageType: string
  content: string
  intent?: string
  knowledgeBaseId?: number
  needHumanIntervention: boolean
  processingTime: number
  createTime: string
}

// 服务评价请求
export interface AiServiceEvaluationRequest {
  sessionId: string
  customerId: number
  evaluationType: number
  satisfactionScore: number
  responseSpeedScore: number
  solutionQualityScore: number
  serviceAttitudeScore: number
  feedbackContent?: string
  improvementSuggestions?: string
}

// 服务工单请求
export interface AiServiceTicketRequest {
  sessionId: string
  customerId: number
  customerName: string
  customerContact?: string
  issueType: string
  issueTitle: string
  issueDescription: string
  priority: number
}

// 知识库项目
export interface AiKnowledgeBaseVO {
  id: number
  title: string
  content: string
  category: string
  keywords: string
  intent: string
  priority: number
  hitCount: number
  isActive: boolean
  createTime: string
  updateTime: string
}

// 转接人工请求
export interface TransferToHumanRequest {
  reason?: string
  assignedTo?: number
}

// 分页响应
export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// ==================== API 接口方法 ====================

/**
 * 发送客户消息 - AI聊天核心接口
 */
export const sendCustomerMessage = (data: AiCustomerChatRequest) => {
  console.log('发送AI聊天消息:', data)
  
  return aiPost<AiCustomerChatResponse>('/ai/customer/chat', data)
}

/**
 * 获取或创建客户会话 - 使用专用请求工具解决跨域问题
 */
export const getOrCreateSession = (customerId: number, customerName: string, customerContact?: string) => {
  console.log('发起会话请求:', { customerId, customerName, customerContact })
  
  return aiGet<AiCustomerSessionVO>('/ai/customer/session', {
    customerId, 
    customerName,
    customerContact
  })
}

/**
 * 获取会话详情
 */
export const getSessionDetail = (sessionId: string) => {
  return request<AiCustomerSessionVO>({
    url: `/ai/customer/session/${sessionId}`,
    method: 'get'
  })
}

/**
 * 获取会话历史消息
 */
export const getSessionHistory = (sessionId: string, page: number = 1, size: number = 20) => {
  console.log('获取会话历史:', { sessionId, page, size })
  
  return aiGet<any[]>(`/ai/customer/session/${sessionId}/history`, { page, size })
}

/**
 * 结束会话
 */
export const endSession = (sessionId: string) => {
  return request<boolean>({
    url: `/ai/customer/session/${sessionId}/end`,
    method: 'post'
  })
}

/**
 * 转接人工客服
 */
export const transferToHuman = (sessionId: string, reason?: string, assignedTo?: number) => {
  return request<void>({
    url: `/ai/customer/session/${sessionId}/transfer`,
    method: 'post',
    data: { reason, assignedTo }
  })
}

/**
 * 搜索知识库
 */
export const searchKnowledgeBase = (keyword: string, category?: string) => {
  return request<AiKnowledgeBaseVO[]>({
    url: '/ai/customer/knowledge/search',
    method: 'get',
    params: { 
      keyword, 
      category 
    }
  })
}

/**
 * 根据意图获取知识库
 */
export const getKnowledgeByIntent = (intent: string) => {
  return request<AiKnowledgeBaseVO[]>({
    url: `/ai/customer/knowledge/intent/${intent}`,
    method: 'get'
  })
}

/**
 * 提交服务评价
 */
export const submitServiceEvaluation = (data: AiServiceEvaluationRequest) => {
  return request<boolean>({
    url: '/ai/customer/evaluation',
    method: 'post',
    data
  })
}

/**
 * 创建服务工单
 */
export const createServiceTicket = (data: AiServiceTicketRequest) => {
  return request<string>({
    url: '/ai/customer/ticket',
    method: 'post',
    data
  })
}

/**
 * 获取客户工单列表
 */
export const getCustomerTickets = (customerId: number, status?: number, page: number = 1, size: number = 10) => {
  return request<PageResponse<any>>({
    url: '/ai/customer/tickets',
    method: 'get',
    params: { 
      customerId, 
      status, 
      page, 
      size 
    }
  })
}

/**
 * 获取需要人工介入的会话列表
 */
export const getHumanInterventionSessions = (page: number = 1, size: number = 20) => {
  return request<PageResponse<AiCustomerSessionVO>>({
    url: '/ai/customer/sessions/human-intervention',
    method: 'get',
    params: { page, size }
  })
}

/**
 * 获取所有会话列表（管理员用）
 */
export const getAllSessions = (page: number = 1, size: number = 20, status?: number) => {
  return request<PageResponse<AiCustomerSessionVO>>({
    url: '/ai/customer/sessions',
    method: 'get',
    params: { page, size, status }
  })
}

/**
 * 获取知识库列表（管理员用）
 */
export const getKnowledgeBaseList = (page: number = 1, size: number = 20, category?: string) => {
  return request<PageResponse<AiKnowledgeBaseVO>>({
    url: '/ai/customer/knowledge',
    method: 'get',
    params: { page, size, category }
  })
}

/**
 * 创建知识库条目（管理员用）
 */
export const createKnowledgeBase = (data: Partial<AiKnowledgeBaseVO>) => {
  return request<number>({
    url: '/ai/customer/knowledge',
    method: 'post',
    data
  })
}

/**
 * 更新知识库条目（管理员用）
 */
export const updateKnowledgeBase = (id: number, data: Partial<AiKnowledgeBaseVO>) => {
  return request<boolean>({
    url: `/ai/customer/knowledge/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除知识库条目（管理员用）
 */
export const deleteKnowledgeBase = (id: number) => {
  return request<boolean>({
    url: `/ai/customer/knowledge/${id}`,
    method: 'delete'
  })
}

/**
 * 获取服务统计数据（管理员用）
 */
export const getServiceStatistics = (startDate?: string, endDate?: string) => {
  return request<any>({
    url: '/ai/customer/statistics',
    method: 'get',
    params: { startDate, endDate }
  })
}

// ==================== 工具方法 ====================

/**
 * 消息类型枚举
 */
export const MessageType = {
  TEXT: 1,
  IMAGE: 2,
  VOICE: 3,
  FILE: 4
} as const

/**
 * 会话状态枚举
 */
export const SessionStatus = {
  ACTIVE: 1,      // 进行中
  ENDED: 2,       // 已结束
  TRANSFERRED: 3  // 已转人工
} as const

/**
 * 评价类型枚举
 */
export const EvaluationType = {
  SATISFACTION: 1,  // 满意度评价
  QUALITY: 2,       // 质量评价
  SPEED: 3          // 速度评价
} as const

/**
 * 工单优先级枚举
 */
export const TicketPriority = {
  LOW: 1,     // 低
  MEDIUM: 2,  // 中
  HIGH: 3,    // 高
  URGENT: 4   // 紧急
} as const

/**
 * 获取状态文本
 */
export const getStatusText = (status: number): string => {
  switch (status) {
    case SessionStatus.ACTIVE:
      return '进行中'
    case SessionStatus.ENDED:
      return '已结束'
    case SessionStatus.TRANSFERRED:
      return '已转人工'
    default:
      return '未知状态'
  }
}

/**
 * 获取优先级文本
 */
export const getPriorityText = (priority: number): string => {
  switch (priority) {
    case TicketPriority.LOW:
      return '低'
    case TicketPriority.MEDIUM:
      return '中'
    case TicketPriority.HIGH:
      return '高'
    case TicketPriority.URGENT:
      return '紧急'
    default:
      return '未知'
  }
}

/**
 * 格式化时间
 */
export const formatTime = (time: string): string => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 60000) { // 1分钟内
    return '刚刚'
  } else if (diff < 3600000) { // 1小时内
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) { // 24小时内
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }
}

/**
 * 验证消息内容
 */
export const validateMessage = (message: string): boolean => {
  return !!(message && message.trim().length > 0 && message.trim().length <= 1000)
}

/**
 * 创建默认的聊天请求
 */
export const createChatRequest = (
  customerId: number,
  customerName: string,
  message: string,
  sessionId?: string,
  customerContact?: string
): AiCustomerChatRequest => {
  return {
    sessionId,
    customerId,
    customerName,
    message: message.trim(),
    messageType: MessageType.TEXT,
    customerContact
  }
}