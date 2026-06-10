export interface DesignRequest {
  description: string
  roomType: string
  preferredStyle?: string
  budget: string
  area?: number
}

export interface StyleMatch {
  styleName: string
  matchScore: number
  reason: string
}

export interface DesignResponse {
  recommendedStyle: string
  styleDescription: string
  colorScheme: string
  furnitureGuide: string
  decorTips: string
  alternativeStyles: StyleMatch[]
}

export interface ChatRequest {
  message: string
  roomType?: string
  preferredStyle?: string
  budget?: string
}

export interface ChatResponse {
  reply: string
  references: string[]
  fromKnowledgeBase: boolean
}

export type AnalyzeStatus = 'idle' | 'loading' | 'done' | 'error'
export type ChatStatus = 'idle' | 'loading' | 'streaming' | 'done' | 'error'
