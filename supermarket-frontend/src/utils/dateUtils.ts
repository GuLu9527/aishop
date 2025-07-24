/**
 * 日期处理工具类
 * 统一处理前端日期格式化、解析等功能
 * 
 * @author AI Assistant
 * @since 2024-01-01
 */

/**
 * 日期格式常量
 */
export const DATE_FORMATS = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm:ss',
  MONTH: 'YYYY-MM',
  YEAR: 'YYYY'
} as const

/**
 * 日期选择器快捷选项 - 日期时间范围
 */
export const dateTimeRangeShortcuts = [
  {
    text: '今天',
    value: () => {
      const start = new Date()
      start.setHours(0, 0, 0, 0)
      const end = new Date()
      end.setHours(23, 59, 59, 999)
      return [start, end]
    }
  },
  {
    text: '昨天',
    value: () => {
      const start = new Date()
      start.setDate(start.getDate() - 1)
      start.setHours(0, 0, 0, 0)
      const end = new Date()
      end.setDate(end.getDate() - 1)
      end.setHours(23, 59, 59, 999)
      return [start, end]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      end.setHours(23, 59, 59, 999)
      const start = new Date()
      start.setDate(start.getDate() - 6)
      start.setHours(0, 0, 0, 0)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      end.setHours(23, 59, 59, 999)
      const start = new Date()
      start.setDate(start.getDate() - 29)
      start.setHours(0, 0, 0, 0)
      return [start, end]
    }
  },
  {
    text: '本月',
    value: () => {
      const start = new Date()
      start.setDate(1)
      start.setHours(0, 0, 0, 0)
      const end = new Date()
      end.setMonth(end.getMonth() + 1, 0)
      end.setHours(23, 59, 59, 999)
      return [start, end]
    }
  },
  {
    text: '上月',
    value: () => {
      const start = new Date()
      start.setMonth(start.getMonth() - 1, 1)
      start.setHours(0, 0, 0, 0)
      const end = new Date()
      end.setDate(0)
      end.setHours(23, 59, 59, 999)
      return [start, end]
    }
  }
]

/**
 * 日期选择器快捷选项 - 单个日期时间
 */
export const singleDateTimeShortcuts = [
  {
    text: '现在',
    value: () => new Date()
  },
  {
    text: '今天开始',
    value: () => {
      const date = new Date()
      date.setHours(0, 0, 0, 0)
      return date
    }
  },
  {
    text: '今天结束',
    value: () => {
      const date = new Date()
      date.setHours(23, 59, 59, 999)
      return date
    }
  }
]

/**
 * 日期选择器快捷选项 - 日期范围
 */
export const dateRangeShortcuts = [
  {
    text: '今天',
    value: () => {
      const date = new Date()
      return [date, date]
    }
  },
  {
    text: '昨天',
    value: () => {
      const date = new Date()
      date.setDate(date.getDate() - 1)
      return [date, date]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 6)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 29)
      return [start, end]
    }
  },
  {
    text: '本月',
    value: () => {
      const start = new Date()
      start.setDate(1)
      const end = new Date()
      end.setMonth(end.getMonth() + 1, 0)
      return [start, end]
    }
  },
  {
    text: '上月',
    value: () => {
      const start = new Date()
      start.setMonth(start.getMonth() - 1, 1)
      const end = new Date()
      end.setDate(0)
      return [start, end]
    }
  }
]

/**
 * 解析后端返回的日期数据
 * 支持字符串格式和数组格式
 */
export function parseBackendDate(dateTime: string | number[] | Date | null | undefined): Date | null {
  if (!dateTime) return null

  try {
    // 处理Date对象
    if (dateTime instanceof Date) {
      return dateTime
    }

    // 处理后端返回的数组格式 [2025, 7, 18, 11, 24, 8]
    if (Array.isArray(dateTime)) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateTime
      // 注意：JavaScript的月份是从0开始的，所以需要减1
      return new Date(year, month - 1, day, hour, minute, second)
    }

    // 处理字符串格式
    if (typeof dateTime === 'string') {
      return new Date(dateTime)
    }

    return null
  } catch (error) {
    console.error('日期解析失败:', dateTime, error)
    return null
  }
}

/**
 * 格式化日期时间 - 标准格式
 * 输出格式：2025-07-21 16:45:30
 */
export function formatDateTime(dateTime: string | number[] | Date | null | undefined): string {
  const date = parseBackendDate(dateTime)
  if (!date || isNaN(date.getTime())) {
    return ''
  }

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

/**
 * 格式化日期 - 仅日期部分
 * 输出格式：2025-07-21
 */
export function formatDate(dateTime: string | number[] | Date | null | undefined): string {
  const date = parseBackendDate(dateTime)
  if (!date || isNaN(date.getTime())) {
    return ''
  }

  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/**
 * 格式化时间 - 仅时间部分
 * 输出格式：16:45
 */
export function formatTime(dateTime: string | number[] | Date | null | undefined): string {
  const date = parseBackendDate(dateTime)
  if (!date || isNaN(date.getTime())) {
    return ''
  }

  const hour = date.getHours()
  const minute = date.getMinutes()
  return `${hour}:${minute.toString().padStart(2, '0')}`
}

/**
 * 增强的日期格式化 - 智能显示
 * 根据日期关系显示不同格式：今天、昨天、本年、跨年
 */
export function formatDateEnhanced(dateTime: string | number[] | Date | null | undefined): string {
  const date = parseBackendDate(dateTime)
  if (!date || isNaN(date.getTime())) {
    return ''
  }

  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000)
  const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate())

  // 判断日期关系
  if (dateOnly.getTime() === today.getTime()) {
    return '今天'
  } else if (dateOnly.getTime() === yesterday.getTime()) {
    return '昨天'
  } else {
    const year = date.getFullYear()
    const currentYear = now.getFullYear()
    
    if (year === currentYear) {
      // 同年只显示月日
      return `${date.getMonth() + 1}-${date.getDate()}`
    } else {
      // 不同年显示完整日期
      return `${year}-${date.getMonth() + 1}-${date.getDate()}`
    }
  }
}

/**
 * 获取相对时间描述
 * 输出格式：刚刚、5分钟前、2小时前、3天前
 */
export function getRelativeTime(dateTime: string | number[] | Date | null | undefined): string {
  const date = parseBackendDate(dateTime)
  if (!date || isNaN(date.getTime())) {
    return ''
  }

  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffMinutes < 1) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return formatDate(date)
  }
}

/**
 * 验证日期是否为未来日期
 */
export function isFutureDate(date: Date): boolean {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date > today
}

/**
 * 禁用未来日期的函数（用于日期选择器）
 */
export function disableFutureDate(time: Date): boolean {
  return isFutureDate(time)
}

/**
 * 格式化为后端需要的字符串格式
 */
export function formatForBackend(date: Date | null | undefined, includeTime = false): string {
  if (!date || isNaN(date.getTime())) {
    return ''
  }

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  if (includeTime) {
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } else {
    return `${year}-${month}-${day}`
  }
}
