package com.stone.aiexam.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.QuestionQueryDTO;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "管理端-题目")
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;


    @Operation(summary = "新增题目")
    @PostMapping
    public Result<Void> add(@RequestBody Question question) {
        questionService.addQuestion(question);
        log.info("新增题目: {}", question);
        return Result.success();
    }

    @Operation(summary = "更新题目")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        questionService.updateQuestion(question);
        log.info("更新题目: {}", question);
        return Result.success();
    }

    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        log.info("删除题目id: {}", id);
        return Result.success();
    }
}