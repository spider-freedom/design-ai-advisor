package com.designadvisor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "请输入问题")
    private String message;

    // Context: previous design preferences for better recommendations
    private String roomType;
    private String preferredStyle;
    private String budget;
}
