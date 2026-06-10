package com.designadvisor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DesignRequest {
    /** Room description or design requirement */
    @NotBlank(message = "请输入房间描述")
    private String description;

    /** Room type: living_room, bedroom, kitchen, bathroom, study, balcony, full_house */
    private String roomType = "living_room";

    /** Preferred style (optional, AI will recommend if empty) */
    private String preferredStyle;

    /** Budget level: economy, standard, premium, luxury */
    private String budget = "standard";

    /** Floor area in square meters */
    private Double area;
}
