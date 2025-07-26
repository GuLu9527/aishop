import axios from 'axios'

export interface AiTestRequest {
  prompt: string
  chatId?: string
}

/**
 * AI服务测试接口 - 支持流式响应
 */
export const testAiService = async (data: AiTestRequest): Promise<ReadableStream<Uint8Array>> => {
  const params = new URLSearchParams()
  params.append('prompt', data.prompt)
  params.append('chatId', data.chatId || 'test-chat-' + Date.now())

  const response = await axios.post('http://localhost:8080/api/ai-test/service', params, {
    responseType: 'stream',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    timeout: 30000, // 30秒超时
  })

  return response.data
}