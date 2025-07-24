/**
 * 简化的markdown渲染器，不依赖外部库
 */

/**
 * 渲染markdown文本
 */
export function renderMarkdown(text: string): string {
  if (!text) return ''

  try {
    let html = text

    // 处理标题
    html = html.replace(/^### (.*$)/gm, '<h3 class="markdown-heading markdown-h3">$1</h3>')
    html = html.replace(/^## (.*$)/gm, '<h2 class="markdown-heading markdown-h2">$1</h2>')
    html = html.replace(/^# (.*$)/gm, '<h1 class="markdown-heading markdown-h1">$1</h1>')

    // 处理粗体
    html = html.replace(/\*\*(.*?)\*\*/g, '<strong class="markdown-strong">$1</strong>')

    // 处理斜体
    html = html.replace(/\*(.*?)\*/g, '<em class="markdown-em">$1</em>')

    // 处理行内代码
    html = html.replace(/`([^`]+)`/g, '<code>$1</code>')

    // 处理链接
    html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" class="markdown-link" target="_blank" rel="noopener noreferrer">$1</a>')

    // 处理无序列表
    html = html.replace(/^- (.*$)/gm, '<li>$1</li>')
    html = html.replace(/(<li>.*<\/li>)/s, '<ul class="markdown-list">$1</ul>')

    // 处理有序列表
    html = html.replace(/^\d+\. (.*$)/gm, '<li>$1</li>')

    // 处理段落
    const lines = html.split('\n')
    const processedLines = lines.map(line => {
      line = line.trim()
      if (!line) return '<br>'
      if (line.startsWith('<h') || line.startsWith('<ul') || line.startsWith('<ol') ||
          line.startsWith('<li') || line.startsWith('<table') || line.startsWith('<div')) {
        return line
      }
      return `<p class="markdown-paragraph">${line}</p>`
    })

    html = processedLines.join('\n')

    // 清理多余的换行
    html = html.replace(/\n+/g, '\n').trim()

    return html
  } catch (error) {
    console.error('Markdown rendering error:', error)
    return text.replace(/\n/g, '<br>')
  }
}

/**
 * 格式化AI回复内容
 */
export function formatAiReply(content: string): string {
  if (!content) return ''
  
  // 预处理：处理特殊格式
  let formatted = content
    // 处理数据表格
    .replace(/(\d+\.?\d*)\s*([万千百十]?[元件个台套批])/g, '<span class="number-unit">$1$2</span>')
    // 处理百分比
    .replace(/(\d+\.?\d*)%/g, '<span class="percentage">$1%</span>')
    // 处理日期
    .replace(/(\d{4}[-年]\d{1,2}[-月]\d{1,2}[日]?)/g, '<span class="date">$1</span>')
    // 处理时间
    .replace(/(\d{1,2}:\d{2}(:\d{2})?)/g, '<span class="time">$1</span>')
    // 处理货币
    .replace(/(￥|¥)(\d+\.?\d*)/g, '<span class="currency">$1$2</span>')
  
  return renderMarkdown(formatted)
}

/**
 * 提取消息中的关键信息
 */
export function extractKeyInfo(content: string) {
  const keyInfo = {
    numbers: [] as string[],
    dates: [] as string[],
    currencies: [] as string[],
    percentages: [] as string[]
  }
  
  // 提取数字
  const numbers = content.match(/\d+\.?\d*\s*[万千百十]?[元件个台套批]/g)
  if (numbers) keyInfo.numbers = numbers
  
  // 提取日期
  const dates = content.match(/\d{4}[-年]\d{1,2}[-月]\d{1,2}[日]?/g)
  if (dates) keyInfo.dates = dates
  
  // 提取货币
  const currencies = content.match(/(￥|¥)\d+\.?\d*/g)
  if (currencies) keyInfo.currencies = currencies
  
  // 提取百分比
  const percentages = content.match(/\d+\.?\d*%/g)
  if (percentages) keyInfo.percentages = percentages
  
  return keyInfo
}

/**
 * 复制到剪贴板（全局函数）
 */
declare global {
  interface Window {
    copyToClipboard: (button: HTMLElement) => void
  }
}

window.copyToClipboard = function(button: HTMLElement) {
  const codeBlock = button.closest('.code-block')
  const code = codeBlock?.querySelector('code')?.textContent
  
  if (code) {
    navigator.clipboard.writeText(code).then(() => {
      const originalText = button.innerHTML
      button.innerHTML = '<i class="el-icon-check"></i> 已复制'
      button.classList.add('copied')
      
      setTimeout(() => {
        button.innerHTML = originalText
        button.classList.remove('copied')
      }, 2000)
    }).catch(err => {
      console.error('复制失败:', err)
    })
  }
}
