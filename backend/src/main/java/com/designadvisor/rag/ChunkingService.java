package com.designadvisor.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChunkingService {

    private final RagProperties ragProperties;

    public List<String> chunk(String text) {
        return chunk(text, ragProperties.getChunkSize(), ragProperties.getChunkOverlap());
    }

    public List<String> chunk(String text, int maxChars, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) return chunks;

        // Split by paragraphs first
        String[] paragraphs = text.split("\\n\\s*\\n");
        StringBuilder current = new StringBuilder();

        for (String para : paragraphs) {
            String trimmed = para.trim();
            if (trimmed.isEmpty()) continue;

            if (current.length() + trimmed.length() > maxChars && current.length() > 0) {
                chunks.add(current.toString().trim());
                // Overlap: keep last `overlap` chars
                if (overlap > 0 && current.length() > overlap) {
                    current = new StringBuilder(current.substring(current.length() - overlap));
                } else {
                    current = new StringBuilder();
                }
            }
            if (current.length() > 0) current.append("\n\n");
            current.append(trimmed);

            // Handle very long paragraphs
            while (current.length() > maxChars) {
                int splitPoint = findSplitPoint(current.toString(), maxChars);
                chunks.add(current.substring(0, splitPoint).trim());
                String remaining = current.substring(Math.max(0, splitPoint - overlap));
                current = new StringBuilder(remaining);
            }
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString().trim());
        }
        return chunks;
    }

    private int findSplitPoint(String text, int maxChars) {
        int pos = maxChars;
        // Try to split at sentence boundary
        for (char sep : new char[]{'。', '！', '？', '\n', '；', '，', '、'}) {
            int idx = text.lastIndexOf(sep, maxChars);
            if (idx > maxChars * 0.5) {
                pos = idx + 1;
                break;
            }
        }
        return Math.min(pos, text.length());
    }
}
