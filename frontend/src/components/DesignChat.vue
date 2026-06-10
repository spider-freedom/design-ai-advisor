<template>
  <div class="design-chat">
    <div class="chat-panel">
      <div class="chat-header">
        <h3>💬 设计顾问对话</h3>
        <p>基于设计知识库的 RAG 智能问答，回答更专业、更具体</p>
      </div>

      <!-- Messages -->
      <div class="chat-messages" ref="msgContainer">
        <div v-if="messages.length === 0 && store.chatStatus === 'idle'" class="chat-placeholder">
          <div class="placeholder-icon">🏡</div>
          <p>向我提问任何关于室内设计的问题</p>
          <div class="hint-questions">
            <span>试试问：</span>
            <el-tag
              v-for="q in hints"
              :key="q"
              class="hint-tag"
              @click="sendMessage(q)"
            >{{ q }}</el-tag>
          </div>
        </div>

        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          :class="['chat-msg', msg.role === 'user' ? 'msg-user' : 'msg-ai']"
        >
          <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🏠' }}</div>
          <div class="msg-bubble">
            <div class="msg-content">{{ msg.content }}</div>
            <div v-if="msg.refs?.length" class="msg-refs">
              <el-tag v-for="r in msg.refs" :key="r" size="small" type="warning">{{ r }}</el-tag>
              <span class="ref-note">来源于知识库</span>
            </div>
          </div>
        </div>

        <div v-if="store.chatStatus === 'streaming'" class="chat-msg msg-ai">
          <div class="msg-avatar">🏠</div>
          <div class="msg-bubble">
            <div class="typing-dots"><i /><i /><i /></div>
          </div>
        </div>

        <div v-if="store.chatStatus === 'error'" class="chat-error">
          ❌ {{ store.chatError }}
        </div>
      </div>

      <!-- Input -->
      <div class="chat-input">
        <el-input
          v-model="inputText"
          placeholder="输入你的设计问题..."
          @keyup.enter="sendMessage(inputText)"
          :disabled="store.chatStatus === 'streaming'"
        >
          <template #append>
            <el-button
              :icon="'Send'"
              @click="sendMessage(inputText)"
              :loading="store.chatStatus === 'streaming'"
              :disabled="!inputText.trim()"
            />
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useDesignStore } from '@/stores/design'
import { chatWithDesigner } from '@/api/design'
import type { ChatResponse } from '@/types/design'

const store = useDesignStore()
const inputText = ref('')
const msgContainer = ref<HTMLElement>()

interface Message {
  role: 'user' | 'ai'
  content: string
  refs?: string[]
}

const messages = ref<Message[]>([])
const hints = [
  '小户型客厅怎么设计显得空间大？',
  '北欧风和日式风有什么区别？',
  '卧室用什么颜色搭配有助于睡眠？',
]

let abortFn: (() => void) | null = null

function sendMessage(text: string) {
  const msg = text.trim()
  if (!msg) return

  messages.value.push({ role: 'user', content: msg })
  inputText.value = ''
  store.setChatStreaming()

  scrollToBottom()

  abortFn?.()
  abortFn = chatWithDesigner(
    { message: msg },
    (chunk) => {
      // streaming chunk - not used in current impl
    },
    (resp: ChatResponse) => {
      messages.value.push({
        role: 'ai',
        content: resp.reply,
        refs: resp.references,
      })
      store.setChatResult(resp.reply, resp.references)
      scrollToBottom()
    },
    (err: string) => {
      store.setChatError(err)
    },
  )
}

function scrollToBottom() {
  nextTick(() => {
    if (msgContainer.value) {
      msgContainer.value.scrollTop = msgContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.design-chat { max-width: 800px; }

.chat-panel {
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(60, 36, 21, 0.06);
  overflow: hidden;
}

.chat-header {
  padding: 20px 24px;
  border-bottom: 1px solid var(--border);
}

.chat-header h3 { font-size: 17px; margin-bottom: 4px; }
.chat-header p { font-size: 12px; color: var(--text-secondary); }

.chat-messages {
  height: 400px;
  overflow-y: auto;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-placeholder {
  text-align: center;
  padding: 48px 0;
  color: var(--text-secondary);
}

.placeholder-icon { font-size: 48px; margin-bottom: 12px; }

.hint-questions {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

.hint-tag { cursor: pointer; }

.chat-msg {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.msg-user { align-self: flex-end; flex-direction: row-reverse; }
.msg-ai { align-self: flex-start; }

.msg-avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
  background: #FBF7F4;
}

.msg-bubble {
  background: #FBF7F4;
  border-radius: 12px;
  padding: 12px 16px;
}

.msg-user .msg-bubble {
  background: var(--brand);
  color: #fff;
}

.msg-content {
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.msg-refs {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.ref-note {
  font-size: 11px;
  color: var(--text-secondary);
}

.chat-error {
  text-align: center;
  color: var(--error);
  font-size: 13px;
}

.chat-input {
  padding: 16px 24px;
  border-top: 1px solid var(--border);
}

.chat-input :deep(.el-input-group__append) {
  background: var(--brand);
  border-color: var(--brand);
}
.chat-input :deep(.el-input-group__append .el-button) {
  color: #fff;
}

.typing-dots {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}
.typing-dots i {
  width: 6px; height: 6px;
  background: var(--text-secondary);
  border-radius: 50%;
  animation: dotPulse 1.4s infinite;
}
.typing-dots i:nth-child(2) { animation-delay: 0.2s; }
.typing-dots i:nth-child(3) { animation-delay: 0.4s; }
@keyframes dotPulse {
  0%, 60%, 100% { opacity: 0.3; }
  30% { opacity: 1; }
}
</style>
