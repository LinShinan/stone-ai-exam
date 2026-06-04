package com.stone.aiexam.controller.common;

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

    @Operation(summary = "题目列表")
    @GetMapping("/list")
    public Result<Page<Question>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            QuestionQueryDTO queryDTO) {
        Page<Question> questionPage = new Page<>(page, size);
        questionService.pageQueryQuestionList(questionPage, queryDTO);
        log.info("全部数据有{}条, 当前页: {}/{}", questionPage.getTotal(), questionPage.getCurrent(), questionPage.getSize());
        return Result.success(questionPage);
    }
}
