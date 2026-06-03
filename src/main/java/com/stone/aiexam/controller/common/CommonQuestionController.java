package com.stone.aiexam.controller.common;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "公共端-题目")
@RequestMapping("/api/common/questions")
public class CommonQuestionController {

    @Autowired
    private QuestionService questionService;

    @Operation(summary = "热门题目列表")
    @GetMapping("/popular")
    public Result<List<Question>> getPopularList(@RequestParam(defaultValue = "6") Integer size) {
        List<Question> list = questionService.getPopularQuestionList(size);
        log.info("热门题目列表: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "题目详情")
    @GetMapping("/{id}")
    public Result<Question> getById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        log.info("id={}的题目详情: {}", id, question);
        return Result.success(question);
    }
}