package com.designadvisor.controller;

import com.designadvisor.dto.ChatRequest;
import com.designadvisor.dto.ChatResponse;
import com.designadvisor.dto.DesignRequest;
import com.designadvisor.dto.DesignResponse;
import com.designadvisor.service.DesignService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;

    @PostConstruct
    public void init() {
        // Initialize RAG index on startup
        designService.initRagIndex();
    }

    @PostMapping("/analyze")
    public DesignResponse analyze(@Valid @RequestBody DesignRequest request) {
        return designService.analyze(request);
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@Valid @RequestBody ChatRequest request) {
        return designService.chatStream(request);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
