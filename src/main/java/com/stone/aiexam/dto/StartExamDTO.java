package com.stone.aiexam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartExamDTO {

    @NotNull(message = "试卷ID不能为空")
    private Long paperId;
}