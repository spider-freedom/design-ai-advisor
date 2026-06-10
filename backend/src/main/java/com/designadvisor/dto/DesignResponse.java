package com.designadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignResponse {
    private String recommendedStyle;
    private String styleDescription;
    private String colorScheme;
    private String furnitureGuide;
    private String decorTips;
    private List<StyleMatch> alternativeStyles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StyleMatch {
        private String styleName;
        private int matchScore;
        private String reason;
    }
}
