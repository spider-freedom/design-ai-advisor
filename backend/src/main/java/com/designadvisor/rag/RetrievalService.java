package com.designadvisor.rag;

import com.designadvisor.entity.DesignChunk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetrievalService {

    private final RagProperties ragProperties;

    public List<DesignChunk> search(float[] queryVector, List<DesignChunk> chunks) {
        return search(queryVector, chunks, ragProperties.getTopK(), ragProperties.getSimilarityThreshold());
    }

    public List<DesignChunk> search(float[] queryVector, List<DesignChunk> chunks, int topK, double threshold) {
        List<ScoredChunk> scored = new ArrayList<>();

        for (DesignChunk chunk : chunks) {
            float[] chunkVec = chunk.getEmbeddingVector();
            if (chunkVec == null) {
                chunkVec = deserializeIfNeeded(chunk);
            }
            if (chunkVec == null || chunkVec.length == 0) continue;

            double similarity = cosineSimilarity(queryVector, chunkVec);
            if (similarity >= threshold) {
                scored.add(new ScoredChunk(chunk, similarity));
            }
        }

        scored.sort(Comparator.comparingDouble(ScoredChunk::similarity).reversed());

        List<DesignChunk> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, scored.size()); i++) {
            result.add(scored.get(i).chunk);
        }
        return result;
    }

    private float[] deserializeIfNeeded(DesignChunk chunk) {
        if (chunk.getEmbedding() != null && chunk.getEmbedding().length > 0) {
            try {
                java.nio.FloatBuffer buffer = java.nio.ByteBuffer.wrap(chunk.getEmbedding()).asFloatBuffer();
                float[] vector = new float[buffer.remaining()];
                buffer.get(vector);
                chunk.setEmbeddingVector(vector);
                return vector;
            } catch (Exception e) {
                log.warn("Failed to deserialize embedding for chunk {}", chunk.getId());
            }
        }
        return null;
    }

    public static double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        double normProduct = Math.sqrt(normA) * Math.sqrt(normB);
        return normProduct > 0 ? dot / normProduct : 0;
    }

    private record ScoredChunk(DesignChunk chunk, double similarity) {}
}
