package com.stone.aiexam.controller;


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

@CrossOrigin
@Tag(name ="题目管理")
@Slf4j
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;


    /**
     * 获取题目列表
     * @param page
     * @param size
     * @param questionQueryDTO
     * @return
     */
    @Operation(summary = "获取题目列表")
    @GetMapping("/list")
    public Result<Page<Question>> pageQueryQuestionList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            QuestionQueryDTO questionQueryDTO
    ){

        Page<Question> questionPage = new Page<>(page,size);
        //下面Java传参是引用的副本，service方法修改的是同一对象，所以数据会回填
        questionService.pageQueryQuestionList(questionPage,questionQueryDTO);
        log.info("全部数据有{}条,目前查找: page / pageSize = {}/{}", questionPage.getTotal(),questionPage.getCurrent(), questionPage.getSize());
        return Result.success(questionPage);
    }

    /**
     *  根据id获取题目详情
     * @param id
     * @return
     */
    @Operation(summary="根据id获取题目详情")
    @GetMapping("/{id}")
    public Result<Question> getQuestionById(@PathVariable Long id){
        Question question = questionService.getQuestionById(id);
        log.info("id={}的题目详情: {}", id, question);
        return Result.success(question);
    }

    /**
     * 新增题目
     * @param question
     * @return
     */
    @Operation(summary = "新增题目")
    @PostMapping
    public Result<Void> addQuestion(@RequestBody Question question){
        questionService.addQuestion(question);
        log.info("新增题目: {}", question);
        return Result.success();
    }

    /**
     * 更新题目
     * @param id
     * @param question
     * @return
     */
    @Operation(summary = "更新题目")
    @PutMapping("/{id}")
    public Result<Void> updateQuestion(@PathVariable Long id, @RequestBody Question question){
        question.setId(id);
        questionService.updateQuestion(question);
        log.info("更新题目: {}", question);
        return Result.success();
    }

    /**
     * 删除题目
     * @param id
     * @return
     */
    @Operation(summary = "删除题目")
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id){
        questionService.deleteQuestion(id);
        log.info("删除题目id: {}", id);
        return Result.success();
    }


    /**
     * 获取热门题目列表
     * @param size 默认展示6道题目
     * @return
     */
    @Operation(summary = "获取热门题目列表")
    @GetMapping("/popular")
    public Result<List<Question>> getPopularQuestionList(@RequestParam(defaultValue="6") Integer size){
        List<Question> popularQuestionList = questionService.getPopularQuestionList(size);
        log.info("热门题目列表: {}", popularQuestionList);
        return Result.success(popularQuestionList);

    }
}
