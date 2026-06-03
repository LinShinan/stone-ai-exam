package com.stone.aiexam.controller.student;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.StartExamDTO;
import com.stone.aiexam.dto.SubmitAnswerDTO;
import com.stone.aiexam.entity.ExamRecord;
import com.stone.aiexam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "用户端-考试")
@RequestMapping("/api/student/exams")
public class StudentExamController {

    @Autowired
    private ExamService examService;

    @Operation(summary = "开始考试")
    @PostMapping("/start")
    public Result<ExamRecord> start(@RequestBody StartExamDTO dto) {
        ExamRecord record = examService.startExam(dto);
        log.info("开始考试: {}", record);
        return Result.success("开始考试", record);
    }

    @Operation(summary = "考试记录详情")
    @GetMapping("/{id}")
    public Result<ExamRecord> getById(@PathVariable Long id) {
        ExamRecord record = examService.getExamRecordById(id);
        log.info("考试记录详情: {}", record);
        return Result.success(record);
    }

    @Operation(summary = "提交试卷")
    @PostMapping("/{examRecordId}/submit")
    public Result<Void> submit(@PathVariable Integer examRecordId, @RequestBody List<SubmitAnswerDTO> records) {
        examService.submitExam(examRecordId, records);
        log.info("提交试卷，考试记录ID: {}", examRecordId);
        return Result.success();
    }
}