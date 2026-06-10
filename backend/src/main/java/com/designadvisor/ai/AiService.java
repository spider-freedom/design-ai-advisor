package com.designadvisor.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface AiService {
    String chat(String systemPrompt, String userMessage);

    record GeneratedDesignAnalysis(String style, String confidence, String analysis, String recommendation) {}

    record DesignStyleResult(String styleName, String matchReason, String colorScheme,
                             String furnitureGuide, String decorTips) {}

    float[] embed(String text);
}
