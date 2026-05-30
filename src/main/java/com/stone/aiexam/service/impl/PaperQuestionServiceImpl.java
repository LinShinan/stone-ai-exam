package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.entity.PaperQuestion;
import com.stone.aiexam.mapper.PaperQuestionMapper;
import com.stone.aiexam.service.PaperQuestionService;
import org.springframework.stereotype.Service;

@Service
public class PaperQuestionServiceImpl extends ServiceImpl<PaperQuestionMapper, PaperQuestion> implements PaperQuestionService {
}
