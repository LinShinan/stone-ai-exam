package com.stone.aiexam.controller.admin;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.AiGenerateRequestDTO;
import com.stone.aiexam.dto.QuestionImportDTO;
import com.stone.aiexam.service.AiService;
import com.stone.aiexam.service.QuestionService;
import com.stone.aiexam.utils.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "管理端-题目批量")
@RequestMapping("/api/admin/questions/batch")
public class AdminQuestionBatchController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AiService aiService;

    @Operation(summary = "下载导入模板")
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = ExcelUtil.createQuestionTemplate();
        return ResponseEntity.ok()
                .header("content-disposition", "attachment;filename=template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(template);
    }

    @Operation(summary = "预览Excel")
    @PostMapping("/preview-excel")
    public Result<List<QuestionImportDTO>> previewExcel(MultipartFile file) throws IOException {
        List<QuestionImportDTO> list = questionService.previewExcel(file);
        log.info("questionImportDTOs: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "批量导入")
    @PostMapping("/import")
    public Result<String> importBatch(@RequestBody List<QuestionImportDTO> list) {
        String result = questionService.importQuestionBatch(list);
        log.info("result: {}", result);
        return Result.success(result, "批量导入完成");
    }

    @Operation(summary = "AI生成题目")
    @PostMapping("/ai-generate")
    public Result<List<QuestionImportDTO>> aiGenerate(@RequestBody @Validated AiGenerateRequestDTO request) {
        List<QuestionImportDTO> questions = aiService.aiGenerateQuestions(request);
        log.info("ai生成题目...");
        return Result.success(questions);
    }
}