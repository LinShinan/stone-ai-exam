package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.dto.QuestionQueryDTO;
import com.stone.aiexam.entity.Question;

import java.util.List;
import java.util.Map;

public interface QuestionService extends IService<Question> {

    /**
     * 分页查询题目列表
     * @param questionPage
     * @param questionQueryDTO
     */
    void pageQueryQuestionList(Page<Question> questionPage, QuestionQueryDTO questionQueryDTO);

    /**
     * 根据id获取题目详情，包括答案和选项
     * @param id
     * @return
     */
    Question getQuestionById(Long id);

    /**
     * 添加题目
     * @param question
     */
    void addQuestion(Question question);

    /**
     * 更新题目
     * @param question
     */
    void updateQuestion(Question question);

    /**
     * 删除题目
     * @param id
     */
    void deleteQuestion(Long id);

    /**
     * 获取热门题目列表
     * @param size
     * @return
     */
    List<Question> getPopularQuestionList(Integer size);
}
