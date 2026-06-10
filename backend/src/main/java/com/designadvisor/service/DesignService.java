package com.designadvisor.service;

import com.designadvisor.dto.ChatRequest;
import com.designadvisor.dto.ChatResponse;
import com.designadvisor.dto.DesignRequest;
import com.designadvisor.dto.DesignResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface DesignService {
    DesignResponse analyze(DesignRequest request);
    SseEmitter chatStream(ChatRequest request);
    void initRagIndex();
}
