package com.designadvisor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("design_chunk")
public class DesignChunk {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long styleId;
    private Integer chunkIndex;
    private String content;
    private byte[] embedding;
    private Integer tokenCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Transient field - not stored in DB, used for in-memory similarity comparison
    @TableField(exist = false)
    private transient float[] embeddingVector;
}
