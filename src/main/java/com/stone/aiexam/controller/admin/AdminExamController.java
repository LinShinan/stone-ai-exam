package com.stone.aiexam.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.ExamRecord;
import com.stone.aiexam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "管理端-考试")
@RequestMapping("/api/admin/exams")
public class AdminExamController {

    @Autowired
    private ExamService examService;

    @Operation(summary = "考试记录列表")
    @GetMapping("/list")
    public Result<Page<ExamRecord>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            String studentName, Integer status,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<ExamRecord> examRecordPage = new Page<>(page, size);
        examService.pageQueryExamRecord(examRecordPage, studentName, status, startDate, endDate);
        return Result.success(examRecordPage);
    }

    @Operation(summary = "AI批阅")
    @PostMapping("/{examRecordId}/grade")
    public Result<Void> grade(@PathVariable Integer examRecordId) {
        examService.autoGradeExam(examRecordId);
        log.info("AI批阅，考试记录ID: {}", examRecordId);
        return Result.success();
    }

    @Operation(summary = "考试记录详情")
    @GetMapping("/{id}")
    public Result<ExamRecord> getById(@PathVariable Long id) {
        ExamRecord record = examService.getExamRecordById(id);
        log.info("考试记录详情: {}", record);
        return Result.success(record);
    }

    @Operation(summary = "删除考试记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examService.deleteExamRecordById(id);
        return Result.success();
    }
}