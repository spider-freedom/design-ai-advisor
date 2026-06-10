package com.designadvisor.rag;

import com.designadvisor.ai.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingClient {

    private final AiService aiService;

    public float[] embed(String text) {
        return aiService.embed(text);
    }

    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> results = new ArrayList<>();
        for (String text : texts) {
            results.add(aiService.embed(text));
        }
        return results;
    }
}
