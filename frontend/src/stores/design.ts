import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AnalyzeStatus, DesignResponse, ChatStatus, ChatResponse } from '@/types/design'

export const useDesignStore = defineStore('design', () => {
  const analyzeStatus = ref<AnalyzeStatus>('idle')
  const analyzeResult = ref<DesignResponse | null>(null)
  const analyzeError = ref('')

  const chatStatus = ref<ChatStatus>('idle')
  const chatReply = ref('')
  const chatRefs = ref<string[]>([])
  const chatError = ref('')

  function setAnalyzeResult(result: DesignResponse) {
    analyzeResult.value = result
    analyzeStatus.value = 'done'
  }

  function setAnalyzeError(msg: string) {
    analyzeError.value = msg
    analyzeStatus.value = 'error'
  }

  function setAnalyzeLoading() {
    analyzeStatus.value = 'loading'
    analyzeError.value = ''
  }

  function setChatResult(reply: string, refs: string[]) {
    chatReply.value = reply
    chatRefs.value = refs
    chatStatus.value = 'done'
  }

  function setChatError(msg: string) {
    chatError.value = msg
    chatStatus.value = 'error'
  }

  function setChatStreaming() {
    chatStatus.value = 'streaming'
    chatError.value = ''
    chatReply.value = ''
  }

  function resetChat() {
    chatStatus.value = 'idle'
    chatReply.value = ''
    chatRefs.value = []
    chatError.value = ''
  }

  return {
    analyzeStatus, analyzeResult, analyzeError,
    chatStatus, chatReply, chatRefs, chatError,
    setAnalyzeResult, setAnalyzeError, setAnalyzeLoading,
    setChatResult, setChatError, setChatStreaming, resetChat,
  }
})
