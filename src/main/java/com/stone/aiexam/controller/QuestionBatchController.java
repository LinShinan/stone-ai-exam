package com.stone.aiexam.controller;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.QuestionImportDTO;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.service.QuestionService;
import com.stone.aiexam.utils.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@Slf4j
@Tag(name="题目批量处理")
@RestController
@RequestMapping("/api/questions/batch")
public class QuestionBatchController {

    @Autowired
    private QuestionService questionService;

    /**
     * 下载题目导入模板
     * @return
     */
    @Operation(summary="下载题目导入模板")
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate(){
        byte[] questionTemplate = ExcelUtil.createQuestionTemplate();

        ResponseEntity<byte[]> response = ResponseEntity.ok()
                .header("content-disposition", "attachment;filename=template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(questionTemplate);

        return response;
    }

    /**
     * 预览Excel文件
     * @param file
     * @return
     */
    @Operation(summary="预览Excel文件")
    @PostMapping("/preview-excel")
    public Result<List<QuestionImportDTO>> previewExcel(MultipartFile file) throws IOException {
        List<QuestionImportDTO> questionImportDTOs =questionService.previewExcel(file);
        log.info("questionImportDTOs: {}", questionImportDTOs);
        return Result.success(questionImportDTOs);
    }

    /**
     * 批量导入题目
     * @param questionImportDTOs
     * @return
     */
    @Operation(summary="批量导入题目")
    @PostMapping("/import-questions")
    public Result<String> importQuestionBatch(@RequestBody List<QuestionImportDTO> questionImportDTOs){
        String result = questionService.importQuestionBatch(questionImportDTOs);
        log.info("result: {}", result);
        return Result.success(result, "批量导入完成");
    }
}
