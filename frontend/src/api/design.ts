import axios from 'axios'
import type { DesignRequest, DesignResponse, ChatRequest, ChatResponse } from '@/types/design'

const http = axios.create({
  baseURL: '/api',
  timeout: 300000, // 5 min for AI
})

export async function analyzeDesign(req: DesignRequest): Promise<DesignResponse> {
  const { data } = await http.post('/analyze', req)
  return data
}

export function chatWithDesigner(
  req: ChatRequest,
  onChunk: (text: string) => void,
  onResult: (resp: ChatResponse) => void,
  onError: (err: string) => void,
): () => void {
  const controller = new AbortController()

  fetch('/api/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
    signal: controller.signal,
  }).then(async (response) => {
    if (!response.ok) throw new Error(`HTTP ${response.status}`)

    const reader = response.body?.getReader()
    if (!reader) return

    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.slice(5).trim()
          if (!data) continue
          try {
            const parsed = JSON.parse(data)
            if (parsed.reply) {
              onResult(parsed)
            }
          } catch {
            onChunk(data)
          }
        }
        if (line.startsWith('event:error')) {
          onError('AI 服务异常，请稍后重试')
        }
      }
    }
  }).catch((err) => {
    if (err.name !== 'AbortError') {
      onError(err.message || '网络连接失败')
    }
  })

  return () => controller.abort()
}
