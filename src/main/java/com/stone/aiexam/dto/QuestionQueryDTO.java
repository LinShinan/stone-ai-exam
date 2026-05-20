package com.stone.aiexam.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private Long categoryId;
    private String difficulty;
    private String type;
    private String keyword;
}
