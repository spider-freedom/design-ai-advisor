package com.designadvisor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("design_style")
public class DesignStyle {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String description;
    private String features;
    private String colorPalette;
    private String suitableSpaces;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
