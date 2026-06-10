package com.designadvisor.rag;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "design-ai.rag")
public class RagProperties {
    private boolean enabled = true;
    private int chunkSize = 500;
    private int chunkOverlap = 100;
    private int topK = 5;
    private double similarityThreshold = 0.6;
}
