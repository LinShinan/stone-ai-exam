package com.stone.aiexam.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiQuestionResponse {

    private List<QuestionImportDTO> questions;
}
