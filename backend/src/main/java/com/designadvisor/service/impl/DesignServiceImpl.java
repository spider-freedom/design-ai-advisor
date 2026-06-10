package com.designadvisor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.designadvisor.ai.AiService;
import com.designadvisor.dto.ChatRequest;
import com.designadvisor.dto.ChatResponse;
import com.designadvisor.dto.DesignRequest;
import com.designadvisor.dto.DesignResponse;
import com.designadvisor.entity.DesignChunk;
import com.designadvisor.entity.DesignStyle;
import com.designadvisor.mapper.DesignChunkMapper;
import com.designadvisor.mapper.DesignStyleMapper;
import com.designadvisor.rag.*;
import com.designadvisor.service.DesignService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DesignServiceImpl implements DesignService {

    private final AiService aiService;
    private final DesignStyleMapper styleMapper;
    private final DesignChunkMapper chunkMapper;
    private final RagProperties ragProperties;
    private final EmbeddingClient embeddingClient;
    private final RetrievalService retrievalService;
    private final RagContextBuilder ragContextBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public DesignResponse analyze(DesignRequest request) {
        // 1. Get all design styles for matching
        List<DesignStyle> styles = styleMapper.selectList(null);

        // 2. Build analysis prompt
        String styleList = buildStyleList(styles);
        String systemPrompt = buildAnalyzeSystemPrompt(styleList);
        String userPrompt = buildAnalyzeUserPrompt(request);

        // 3. Call AI
        String aiResponse = aiService.chat(systemPrompt, userPrompt);

        // 4. Parse response
        return parseAnalyzeResponse(aiResponse, request);
    }

    @Override
    public SseEmitter chatStream(ChatRequest request) {
        SseEmitter emitter = new SseEmitter(300_000L);

        new Thread(() -> {
            try {
                // RAG retrieval
                String context;
                List<String> refs = new ArrayList<>();

                if (ragProperties.isEnabled()) {
                    float[] queryVec = embeddingClient.embed(request.getMessage());
                    List<DesignChunk> allChunks = chunkMapper.selectList(null);

                    // Deserialize embeddings
                    for (DesignChunk chunk : allChunks) {
                        if (chunk.getEmbeddingVector() == null && chunk.getEmbedding() != null) {
                            chunk.setEmbeddingVector(
                                    com.designadvisor.ai.DeepSeekAiService.deserializeVector(chunk.getEmbedding()));
                        }
                    }

                    List<DesignChunk> retrieved = retrievalService.search(queryVec, allChunks);
                    if (!retrieved.isEmpty()) {
                        for (DesignChunk chunk : retrieved) {
                            DesignStyle style = styleMapper.selectById(chunk.getStyleId());
                            if (style != null) refs.add(style.getName());
                        }
                    }
                    context = ragContextBuilder.build(retrieved, request.getMessage());
                } else {
                    context = "你是一位专业的室内设计师。\n\n用户问题：" + request.getMessage();
                }

                // Call AI streaming
                String fullReply = aiService.chat(context, request.getMessage());

                // Send result
                ChatResponse chatResp = ChatResponse.builder()
                        .reply(fullReply)
                        .references(refs.stream().distinct().toList())
                        .fromKnowledgeBase(!refs.isEmpty())
                        .build();

                emitter.send(SseEmitter.event().name("result").data(objectMapper.writeValueAsString(chatResp)));
                emitter.complete();
            } catch (Exception e) {
                log.error("Chat stream error", e);
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (IOException ex) {
                    log.error("Failed to send error", ex);
                }
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @Override
    public void initRagIndex() {
        log.info("Initializing RAG index for design knowledge base...");
        List<DesignStyle> styles = styleMapper.selectList(null);

        // Check if already indexed
        Long chunkCount = chunkMapper.selectCount(null);
        if (chunkCount > 0) {
            log.info("RAG index already exists ({} chunks), skipping.", chunkCount);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                int totalChunks = 0;
                for (DesignStyle style : styles) {
                    String text = style.getName() + "：" + style.getDescription() + "\n"
                            + "特征：" + style.getFeatures() + "\n"
                            + "色彩：" + style.getColorPalette() + "\n"
                            + "适用空间：" + style.getSuitableSpaces();

                    List<String> chunks = new com.designadvisor.rag.ChunkingService(ragProperties).chunk(text);
                    int idx = 0;
                    for (String chunkText : chunks) {
                        float[] vector = embeddingClient.embed(chunkText);

                        DesignChunk chunk = new DesignChunk();
                        chunk.setStyleId(style.getId());
                        chunk.setChunkIndex(idx++);
                        chunk.setContent(chunkText);
                        chunk.setEmbedding(com.designadvisor.ai.DeepSeekAiService.serializeVector(vector));
                        chunk.setTokenCount(chunkText.length());
                        chunkMapper.insert(chunk);
                        totalChunks++;
                    }
                    // Rate limit: delay between styles to avoid API rate limiting
                    Thread.sleep(500);
                }
                log.info("RAG index complete. Total chunks: {}", totalChunks);
            } catch (Exception e) {
                log.error("RAG indexing failed", e);
            }
        });
    }

    private String buildStyleList(List<DesignStyle> styles) {
        StringBuilder sb = new StringBuilder();
        for (DesignStyle s : styles) {
            sb.append("- ").append(s.getName()).append("（").append(s.getCategory()).append("）：")
                    .append(s.getDescription()).append("\n");
        }
        return sb.toString();
    }

    private String buildAnalyzeSystemPrompt(String styleList) {
        return """
            你是一位资深室内设计师，精通各种设计风格。你的任务是根据用户提供的房间信息和偏好，
            推荐最合适的设计风格，并给出具体的设计方案。

            已知设计风格库：
            %s

            请始终以 JSON 格式回复，格式如下：
            {
              "recommendedStyle": "推荐风格名称",
              "styleDescription": "风格描述（100字内）",
              "colorScheme": "推荐配色方案（含具体色号或颜色名称）",
              "furnitureGuide": "家具选择指南",
              "decorTips": "装饰搭配建议",
              "alternativeStyles": [
                {"styleName": "备选风格1", "matchScore": 85, "reason": "匹配原因"},
                {"styleName": "备选风格2", "matchScore": 70, "reason": "匹配原因"}
              ]
            }
            """.formatted(styleList);
    }

    private String buildAnalyzeUserPrompt(DesignRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("请为以下空间推荐设计风格：\n\n");
        sb.append("房间描述：").append(request.getDescription()).append("\n");
        sb.append("空间类型：").append(roomTypeLabel(request.getRoomType())).append("\n");
        if (request.getPreferredStyle() != null && !request.getPreferredStyle().isBlank()) {
            sb.append("偏好风格：").append(request.getPreferredStyle()).append("\n");
        }
        sb.append("预算水平：").append(budgetLabel(request.getBudget())).append("\n");
        if (request.getArea() != null) {
            sb.append("面积：").append(request.getArea()).append("平方米\n");
        }
        sb.append("\n请推荐最匹配的设计风格并给出具体方案。");
        return sb.toString();
    }

    private DesignResponse parseAnalyzeResponse(String aiResponse, DesignRequest request) {
        try {
            // Extract JSON from response (may contain markdown code blocks)
            String json = aiResponse;
            if (json.contains("```json")) {
                json = json.substring(json.indexOf("```json") + 7);
                if (json.contains("```")) json = json.substring(0, json.indexOf("```"));
            } else if (json.contains("```")) {
                json = json.substring(json.indexOf("```") + 3);
                if (json.contains("```")) json = json.substring(0, json.indexOf("```"));
            }
            json = json.trim();

            JsonNode root = objectMapper.readTree(json);

            List<DesignResponse.StyleMatch> alternatives = new ArrayList<>();
            if (root.has("alternativeStyles")) {
                for (JsonNode alt : root.get("alternativeStyles")) {
                    alternatives.add(DesignResponse.StyleMatch.builder()
                            .styleName(alt.path("styleName").asText())
                            .matchScore(alt.path("matchScore").asInt())
                            .reason(alt.path("reason").asText())
                            .build());
                }
            }

            return DesignResponse.builder()
                    .recommendedStyle(root.path("recommendedStyle").asText())
                    .styleDescription(root.path("styleDescription").asText())
                    .colorScheme(root.path("colorScheme").asText())
                    .furnitureGuide(root.path("furnitureGuide").asText())
                    .decorTips(root.path("decorTips").asText())
                    .alternativeStyles(alternatives)
                    .build();
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse AI response as JSON, returning raw", e);
            return DesignResponse.builder()
                    .recommendedStyle(request.getPreferredStyle() != null ? request.getPreferredStyle() : "现代简约")
                    .styleDescription(aiResponse.substring(0, Math.min(200, aiResponse.length())))
                    .colorScheme("请参考上方分析结果")
                    .furnitureGuide("请参考上方分析结果")
                    .decorTips("请参考上方分析结果")
                    .alternativeStyles(List.of())
                    .build();
        }
    }

    private String roomTypeLabel(String type) {
        return switch (type != null ? type : "") {
            case "living_room" -> "客厅";
            case "bedroom" -> "卧室";
            case "kitchen" -> "厨房";
            case "bathroom" -> "浴室";
            case "study" -> "书房";
            case "balcony" -> "阳台";
            case "full_house" -> "全屋";
            default -> "未指定";
        };
    }

    private String budgetLabel(String budget) {
        return switch (budget != null ? budget : "") {
            case "economy" -> "经济型（简约实用）";
            case "standard" -> "标准型（品质舒适）";
            case "premium" -> "高端型（精致设计）";
            case "luxury" -> "奢华型（顶级配置）";
            default -> "标准型";
        };
    }
}
