package com.stone.aiexam.dto;

import lombok.Data;

/**
 * AI批改单道简答题的返回结果（字段对齐教程，前端依赖此结构）
 */
@Data
public class AiGradingResult {
    /**
     * 该题得分
     */
    private Integer score;
    /**
     * 给学生看的评价反馈（50字以内）
     */
    private String feedback;
    /**
     * 扣分原因或得分依据（30字以内）
     */
    private String reason;
}