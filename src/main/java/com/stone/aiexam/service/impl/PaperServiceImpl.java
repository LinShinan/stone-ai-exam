package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.common.StoneConstant;
import com.stone.aiexam.dto.PaperDTO;
import com.stone.aiexam.dto.RuleDTO;
import com.stone.aiexam.dto.SmartPaperDTO;
import com.stone.aiexam.entity.ExamRecord;
import com.stone.aiexam.entity.Paper;
import com.stone.aiexam.entity.PaperQuestion;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.ExamRecordMapper;
import com.stone.aiexam.mapper.PaperMapper;
import com.stone.aiexam.mapper.QuestionMapper;
import com.stone.aiexam.service.PaperQuestionService;
import com.stone.aiexam.service.PaperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private PaperQuestionService paperQuestionService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;


    /**
     * 创建试卷
     * @param paperDTO
     */
    @Transactional // 事务管理，确保数据一致性
    @Override
    public void addPaper(PaperDTO paperDTO) {
        //1. 校验试卷名称
        checkPaperName(paperDTO.getName(), null);
        //2. 设置基本试卷信息
        Paper paper = new Paper();
        BeanUtils.copyProperties(paperDTO,paper);
        paper.setStatus(StoneConstant.PAPER_STATUS_DRAFT);
        //3. 如果没有试卷题目，设置总分和题目数量为0，保存试卷
        if(CollectionUtils.isEmpty(paperDTO.getQuestions())){
            paper.setTotalScore(BigDecimal.ZERO);
            paper.setQuestionCount(0);
            save(paper);
            return;
        }

        //4. 如果有试卷题目，设置试卷总分和题目数量，先保存试卷来获取试卷id，然后把试卷题目保存到试卷题目表中
        paper.setTotalScore(paperDTO.getQuestions().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        paper.setQuestionCount(paperDTO.getQuestions().size());
        save(paper);
        Long paperId = paper.getId();

        List<PaperQuestion> paperQuestionList = paperDTO.getQuestions().entrySet()
                .stream()
                .map(entry -> new PaperQuestion(paperId.intValue(), entry.getKey().longValue(), entry.getValue()))
                .toList();

        paperQuestionService.saveBatch(paperQuestionList);

    }

    /**
     * 智能组卷
     * @param smartPaperDTO
     */
    @Transactional
    @Override
    public void createSmartPaper(SmartPaperDTO smartPaperDTO) {
        //1. 校验试卷名称
        checkPaperName(smartPaperDTO.getName(), null);

        //2. 设置基本试卷信息,保存获取试卷id方便设置PaperQuestion
        Paper paper = new Paper();
        BeanUtils.copyProperties(smartPaperDTO,paper);
        paper.setStatus(StoneConstant.PAPER_STATUS_DRAFT);
        save(paper);
        Long paperId = paper.getId();

        //3. 根据组卷规则，从题目库中随机选择题目
        List<RuleDTO> rules = smartPaperDTO.getRules();
        if(CollectionUtils.isEmpty(rules)){
            return;
        }

        int totalCount = 0;
        BigDecimal totalScore =BigDecimal.ZERO;

        for(RuleDTO rule: rules){
            //a. 规则或关键字段为空，跳过
            if (ObjectUtils.isEmpty(rule) || rule.getType() == null || rule.getScore() == null) {
                log.warn("智能组卷：规则关键字段缺失，已跳过 rule={}", rule);
                continue;
            }
            //b. 规则下的题目数量无效，跳过
            if (rule.getCount() == null || rule.getCount() <= 0) {
                continue;
            }
            //c. 查询符合题目类型和分类的题目
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Question::getType, rule.getType().name())
                    .in(!CollectionUtils.isEmpty(rule.getCategoryIds()), Question::getCategoryId, rule.getCategoryIds());

            List<Question> questions = questionMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(questions)) {
                log.warn("智能组卷：未找到符合规则的题目 type={}, categoryIds={}", rule.getType(), rule.getCategoryIds());
                continue;
            }
            //d. 获取的题目数 vs 规则下的题目数量，取最小值
            int count = Math.min(questions.size(), rule.getCount());
            if (count < rule.getCount()) {
                log.warn("智能组卷：题库数量不足，要求{}道，实际仅{}道 type={}", rule.getCount(), count, rule.getType());
            }

            //e. 更新题目总数和总分
            totalCount += count;
            totalScore = totalScore.add(BigDecimal.valueOf((long) count * rule.getScore()));
            //f. 打乱题目顺序，随机选择
            Collections.shuffle(questions);
            List<Question> selectedQuestions = questions.subList(0, count);

            //g. 将 Question 转换成 PaperQuestion，保存到试卷题目表
            List<PaperQuestion> paperQuestionList = selectedQuestions.stream()
                    .map(q -> new PaperQuestion(Math.toIntExact(paperId), q.getId(), BigDecimal.valueOf(rule.getScore())))
                    .toList();

            paperQuestionService.saveBatch(paperQuestionList);

        }

        //4. 更新试卷总分和题目数量
        paper.setQuestionCount(totalCount);
        paper.setTotalScore(totalScore);

        updateById(paper);

    }

    /**
     * 更新试卷
     * @param id
     * @param paperDTO
     */
    @Transactional
    @Override
    public void updatePaper(Long id, PaperDTO paperDTO) {
        //1. 如果是发布状态，不允许更新，抛出异常
        Paper paper = getById(id);
        if(paper==null){
            throw new BusinessException("试卷不存在");
        }
        if(StoneConstant.PAPER_STATUS_PUBLISHED.equals(paper.getStatus())){
            throw new BusinessException("试卷已发布，不允许更新");
        }
        //2. 如果更新的试卷名称和其他试卷重复，抛出异常
        checkPaperName(paperDTO.getName(),id);

        //3. 设置基本试卷信息并更新
        BeanUtils.copyProperties(paperDTO,paper);
        Map<Integer,BigDecimal> questions = paperDTO.getQuestions();

        if(CollectionUtils.isEmpty(questions)){
            paper.setQuestionCount(0);
            paper.setTotalScore(BigDecimal.ZERO);
        }else{
            paper.setQuestionCount(questions.size());
            paper.setTotalScore(questions.values().stream().reduce(BigDecimal.ZERO,BigDecimal::add));
        }

        updateById(paper);
        //4. 更新试卷题目列表
        //先删
        LambdaQueryWrapper<PaperQuestion> pqWrapper = new LambdaQueryWrapper<>();
        pqWrapper.eq(PaperQuestion::getPaperId,id);
        paperQuestionService.remove(pqWrapper);
        //后增
        if(!CollectionUtils.isEmpty(questions)){
            List<PaperQuestion> paperQuestionList = questions.entrySet().stream()
                    .map(entry ->
                            new PaperQuestion(id.intValue(), entry.getKey().longValue(), entry.getValue()))
                    .toList();

            paperQuestionService.saveBatch(paperQuestionList);
        }
    }

    /**
     * 根据id删除试卷
     * @param id
     */
    @Override
    public void deletePaperById(Long id) {
        //1. 如果是已发布，不能删除
        Paper paper = getById(id);
        if(paper==null){
            throw new BusinessException("试卷不存在");
        }
        if(StoneConstant.PAPER_STATUS_PUBLISHED.equals(paper.getStatus())){
            throw new BusinessException("试卷已发布，不允许删除");
        }
        //2. 如果是在考试记录中，不能删除
        LambdaQueryWrapper<ExamRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamRecord::getExamId,id);
        Long count = examRecordMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException("试卷在%d条考试记录中，不允许删除".formatted(count));
        }
        //3. 删除试卷
        removeById(id);
    }

    /**
     * 根据id获取试卷详情
     * @param id
     * @return
     */
    @Override
    public Paper getDetailById(Long id) {
        //1. 查询paper普通信息
        Paper paper = getById(id);
        if(paper==null){
            throw new BusinessException("试卷不存在，无法获取试卷详情");
        }
        //2. 查询paper的question集合信息
        //a.题目表，试卷题目表，答案表,选项表多表联合查询
        List<Question> questionList = questionMapper.getQuestionDetailByPaperId(id);
        if(CollectionUtils.isEmpty(questionList)){
            log.info("试卷id={}下没有题目", id);
            return paper;
        }
        //b.题目按照选择，判断，填空排序
        questionList.sort((q1,q2)->
                Integer.compare(getTypeOrder(q1.getType()), getTypeOrder(q2.getType()))
        );
        paper.setQuestions(questionList);

        return paper;
    }

    /**
     * 根据题目类型返回排序顺序
     * @param type
     * @return
     */
    private int getTypeOrder(String type){
        if(type==null){
            return 9;
        }
        return switch (type) {
            case "CHOICE" -> 1;
            case "JUDGMENT" -> 2;
            case "FILL_BLANK" -> 3;
            default -> 9;
        };
    }

    /**
     * 校验试卷名称
     * @param name
     * @param excludeId
     */
    private void checkPaperName(String name, Long excludeId) {
        //判空
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("试卷名称不能为空");
        }
        //判重
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getName, name);
        if (excludeId != null) {
            wrapper.ne(Paper::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException("试卷名称已存在");
        }
    }



}
