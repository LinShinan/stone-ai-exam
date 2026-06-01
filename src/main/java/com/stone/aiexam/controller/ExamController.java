package com.stone.aiexam.controller;

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

@CrossOrigin
@Tag(name="考试管理")
@Slf4j
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * 开始考试
     * @param startExamDTO
     * @return
     */
    @Operation(summary="开始考试")
    @PostMapping("/start")
    public Result<ExamRecord> startExam(@RequestBody StartExamDTO startExamDTO){
        ExamRecord examRecord = examService.startExam(startExamDTO);
        log.info("开始考试，{}", examRecord);
        return Result.success("开始考试", examRecord);
    }

    /**
     * 根据id获取考试记录详情
     * @param id
     * @return
     */
    @Operation(summary="根据id获取考试记录详情")
    @GetMapping("/{id}")
    public Result<ExamRecord> getExamRecordById(@PathVariable Long id){
        ExamRecord examRecord = examService.getExamRecordById(id);
        log.info("获取考试记录详情，{}", examRecord);
        return Result.success(examRecord);
    }


    /**
     * 提交考试试卷
     * @param examRecordId
     * @param records
     * @return
     */
    @Operation(summary="提交考试试卷")
    @PostMapping("/{examRecordId}/submit")
    public Result<Void> submitExam(@PathVariable Integer examRecordId, @RequestBody List<SubmitAnswerDTO> records){
        examService.submitExam(examRecordId,records);
        log.info("提交考试试卷，考试记录ID：{}", examRecordId);
        return Result.success();
    }

    /**
     * AI自动批阅
     * @param examRecordId
     * @return
     */
    @Operation(summary="AI自动批阅")
    @PostMapping("/{examRecordId}/grade")
    public Result<Void> autoGradeExam(@PathVariable Integer examRecordId){
        examService.autoGradeExam(examRecordId);
        log.info("AI自动批阅，考试记录ID：{}", examRecordId);
        return Result.success();
    }

}
