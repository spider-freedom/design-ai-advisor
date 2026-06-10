package com.designadvisor.rag;

import com.designadvisor.entity.DesignChunk;
import com.designadvisor.entity.DesignStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RagContextBuilder {

    public String build(List<DesignChunk> retrievedChunks, String userQuestion) {
        if (retrievedChunks.isEmpty()) {
            return "你是一位专业的室内设计师。请根据你的知识回答用户的问题。\n\n用户问题：" + userQuestion;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("你是一位专业的室内设计师。请优先基于以下「设计参考资料」回答用户问题。");
        sb.append("如果参考资料中没有直接相关的信息，可以结合你的专业知识补充，并说明哪些部分来自参考资料、哪些来自你的专业知识。\n\n");

        sb.append("【设计参考资料】\n");
        // Group chunks by style for better context
        Set<String> styles = retrievedChunks.stream()
                .map(c -> "设计知识 #" + c.getStyleId())
                .collect(Collectors.toSet());

        for (DesignChunk chunk : retrievedChunks) {
            sb.append("--- 设计知识 #").append(chunk.getStyleId());
            if (chunk.getChunkIndex() != null) {
                sb.append(" (片段 ").append(chunk.getChunkIndex()).append(")");
            }
            sb.append(" ---\n");
            sb.append(chunk.getContent()).append("\n\n");
        }

        sb.append("【用户问题】\n").append(userQuestion).append("\n\n");
        sb.append("请提供具体、实用的设计建议。如果涉及颜色搭配，请给出具体的色号或颜色名称。");
        sb.append("如果涉及材质，请说明具体的材质类型和适用场景。");

        return sb.toString();
    }
}
