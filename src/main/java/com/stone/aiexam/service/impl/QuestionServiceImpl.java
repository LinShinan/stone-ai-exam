package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.dto.QuestionImportDTO;
import com.stone.aiexam.dto.QuestionQueryDTO;
import com.stone.aiexam.entity.PaperQuestion;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.entity.QuestionAnswer;
import com.stone.aiexam.entity.QuestionChoice;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.PaperQuestionMapper;
import com.stone.aiexam.mapper.QuestionAnswerMapper;
import com.stone.aiexam.mapper.QuestionChoiceMapper;
import com.stone.aiexam.mapper.QuestionMapper;
import com.stone.aiexam.service.QuestionService;
import com.stone.aiexam.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.stone.aiexam.common.StoneConstant.HOT_QUESTION_KEY;

@Slf4j
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionAnswerMapper questionAnswerMapper;

    @Autowired
    private QuestionChoiceMapper questionChoiceMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private PaperQuestionMapper paperQuestionMapper;


    /**
     * 分页查询题目列表
     * @param questionPage
     * @param questionQueryDTO
     */
    @Override
    public void pageQueryQuestionList(Page<Question> questionPage, QuestionQueryDTO questionQueryDTO) {
        //三张表：a题目表， b答案表,c选项表; 1a1b,1a多c
        //1. 查询题目表，获得题目列表
        Long categoryId = questionQueryDTO.getCategoryId();
        String difficulty = questionQueryDTO.getDifficulty();
        String type = questionQueryDTO.getType();
        String keyword = questionQueryDTO.getKeyword();

        LambdaQueryWrapper<Question> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Question::getCategoryId,categoryId)
                .eq(!ObjectUtils.isEmpty(difficulty),Question::getDifficulty,difficulty)
                .eq(!ObjectUtils.isEmpty(type),Question::getType,type)
                .like(!ObjectUtils.isEmpty(keyword),Question::getTitle,keyword)
                .orderByDesc(Question::getCreateTime);

        page(questionPage,queryWrapper);

        if(ObjectUtils.isEmpty(questionPage.getRecords())){
            return;
        }

        //2. 为questionPage设置题目答案和选项
        setAnswerAndChoice(questionPage.getRecords());
    }

    /**
     * 根据id查询题目信息
     * @param id
     * @return
     */
    @Override
    public Question getQuestionById(Long id) {
        //1. 获取题目基本信息
        Question question = getById(id);
        if(ObjectUtils.isEmpty(question)){
            throw new BusinessException("题目不存在");
        }
        //2. 查询答案并添加到题目中
        QuestionAnswer questionAnswer = questionAnswerMapper.selectOne(new LambdaQueryWrapper<QuestionAnswer>().eq(QuestionAnswer::getQuestionId, id));
        question.setAnswer(questionAnswer);

        //3. 如果是选择题，查询选项并添加到题目中
        if("CHOICE".equals(question.getType())){
            List<QuestionChoice> questionChoices = questionChoiceMapper.selectList(new LambdaQueryWrapper<QuestionChoice>().eq(QuestionChoice::getQuestionId, id));
            questionChoices.sort(Comparator.comparing(QuestionChoice::getSort));
            question.setChoices(questionChoices);
        }

        //4. 通过redis的zset设置题目热度
        incrementHotScore(question.getId());

        return question;
    }


    /**
     * 添加题目
     * @param question
     */
    @Transactional
    @Override
    public void addQuestion(Question question) {
        //1.确保同类型下不能有同名题目
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getType,question.getType())
                        .eq(Question::getTitle,question.getTitle());
        long count = count(queryWrapper);
        if(count>0){
            throw new BusinessException("同类型下已存在同名题目");
        }
        //2. 保存到题目表，获取题目主键id
        save(question);
        Long questionId = question.getId();

        //3. 添加答案，如果是选择题，答案为选项列表；如果是其他题，创建时已有，直接insert就行
        QuestionAnswer questionAnswer = question.getAnswer();
        questionAnswer.setQuestionId(questionId);

        if("CHOICE".equals(question.getType())){
            List<QuestionChoice> choices = question.getChoices();
            StringJoiner sj = new StringJoiner(",");
            for(int i=0;i<choices.size();i++){
                QuestionChoice choice = choices.get(i);
                //4. 题目选项表中添加数据
                choice.setQuestionId(questionId);
                questionChoiceMapper.insert(choice);

                if(choice.getIsCorrect()){
                    sj.add(String.valueOf((char)('A'+i)));
                }
            }
            questionAnswer.setAnswer(sj.toString());
        }

        questionAnswerMapper.insert(questionAnswer);
    }

    /**
     * 修改题目
     * @param question
     */
    @Transactional
    @Override
    public void updateQuestion(Question question) {
        //1. 判断是否和其他题目的title一样，一样不能更新
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getTitle,question.getTitle())
                .eq(Question::getType,question.getType()) //question参数传的是更新的题型（也可能没有修改），不论是否，在当前的Type下不能和其他题的题目一样
                .ne(Question::getId,question.getId());// 排除自己
        long count = count(queryWrapper);
        if(count > 0){
            throw new BusinessException("更新失败，不能和同类型下的题目标题一样");
        }

        //2. 更新题目基本信息
        updateById(question);

        //3. 如果是选择题，更新选项表，并更新对应答案，非选择题直接更新答案
        QuestionAnswer questionAnswer = question.getAnswer();
        if("CHOICE".equals(question.getType())) {
            //先删
            LambdaQueryWrapper<QuestionChoice> choiceWrapper = new LambdaQueryWrapper<QuestionChoice>()
                    .eq(QuestionChoice::getQuestionId, question.getId());
            questionChoiceMapper.delete(choiceWrapper);

            //后增
            List<QuestionChoice> choices = question.getChoices();
            StringJoiner sj = new StringJoiner(",");

            if (!CollectionUtils.isEmpty(choices)) {
                for (int i = 0; i < choices.size(); i++) {
                    QuestionChoice choice = choices.get(i);
                    //因为是逻辑删除，主键还在，原本的id会干扰导致Duplicate Key, 所以id要先置为null
                    choice.setId(null);
                    choice.setCreateTime(null);
                    choice.setUpdateTime(null);
                    choice.setSort(i);
                    choice.setQuestionId(question.getId());
                    questionChoiceMapper.insert(choice);

                    sj.add(String.valueOf((char) ('A' + i)));
                }
                questionAnswer.setAnswer(sj.toString());
            }
        }

        questionAnswerMapper.updateById(questionAnswer);
    }

    /**
     * 删除题目
     * @param id
     */
    @Transactional // 事务管理, 确保数据一致性
    @Override
    public void deleteQuestion(Long id) {
        //1.如果有试卷包含该题目，不能删除
        LambdaQueryWrapper<PaperQuestion> paperQuestionWrapper = new LambdaQueryWrapper<PaperQuestion>();
        paperQuestionWrapper.eq(PaperQuestion::getQuestionId, id);
        Long count = paperQuestionMapper.selectCount(paperQuestionWrapper);
        if(count >0){
            throw new BusinessException("题目已包含在试卷中，不能删除");
        }
        //2.删除题目表对应数据
        removeById(id);
        //3.删除题目对应答案表数据
        LambdaQueryWrapper<QuestionAnswer> answerWrapper = new LambdaQueryWrapper<QuestionAnswer>();
        answerWrapper.eq(QuestionAnswer::getQuestionId, id);
        questionAnswerMapper.delete(answerWrapper);
        //4.如果是选择题，删除题目对应选项表数据
        LambdaQueryWrapper<QuestionChoice> choiceWrapper = new LambdaQueryWrapper<QuestionChoice>();
        choiceWrapper.eq(QuestionChoice::getQuestionId, id);
        questionChoiceMapper.delete(choiceWrapper);
    }


    /**
     * 获取热门题目列表
     * @param size
     * @return
     */
    @Override
    public List<Question> getPopularQuestionList(Integer size) {
        //1. 定义热门题目列表
        ArrayList<Question> popularQuestions = new ArrayList<>();

        //2.获得redis中的题目id, 根据分数从高到低排序，添加对应题目到热门题目列表
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Set<Object> questionIdObjects = zSetOperations.reverseRange(HOT_QUESTION_KEY, 0, size - 1);
        List<Long> idsByRedis = null;
        if(!CollectionUtils.isEmpty(questionIdObjects)){
            idsByRedis = questionIdObjects.stream()
                    .map(o -> Long.valueOf(o.toString()))
                    .toList();

            //转换成题目时，要保证顺序不乱
            List<Question> questions = listByIds(idsByRedis);
            Map<Long,Question> idMap = questions.stream()
                    .collect(Collectors.toMap(Question::getId, question -> question));

            idsByRedis.forEach(questionId ->{
                Question q = idMap.get(questionId);
                //redis缓存的题目可能被删除，所以要判断q是否为null
                if(q!=null){
                    popularQuestions.add(q);
                }else{
                    zSetOperations.remove(HOT_QUESTION_KEY,questionId);
                }
            });
        }

        //3. 判断是否有size个，没有的话补充最新题目，使其有size个
        int needCount = size - popularQuestions.size();
        if(needCount > 0){
            //按照创建时间降序，并且保证不与通过redis加入的题目重复
            LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.notIn(!CollectionUtils.isEmpty(idsByRedis),Question::getId,idsByRedis);//只有当idsByRedis不为空时才执行notIn
            queryWrapper.orderByDesc(Question::getCreateTime);
            queryWrapper.last("LIMIT " + needCount);

            List<Question> newQuestions = list(queryWrapper);
            popularQuestions.addAll(newQuestions);
        }
        //4. 补充题目信息的答案，如果是选择题还要补充选项
        setAnswerAndChoice(popularQuestions);

        return popularQuestions;
    }

    /**
     * 预览Excel
     * @param file
     * @return
     */
    @Override
    public List<QuestionImportDTO> previewExcel(MultipartFile file) throws IOException {
        //1.校验文件是否为空
        if(file.isEmpty()){
            throw new BusinessException("文件不能为空");
        }
        //2.校验文件是否为Excel文件
        String filename = file.getOriginalFilename();
        if(filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))){
            throw new BusinessException("文件格式错误，只能上传.xlsx或.xls文件");
        }
        //3.读取Excel文件
        return ExcelUtil.parseQuestionTemplate(file);
    }


    /**
     * 批量导入题目
     * @param questionImportDTOs
     * @return
     */
    @Override
    public String importQuestionBatch(List<QuestionImportDTO> questionImportDTOs) {
        //1. 确保题目数据不为空
        if(CollectionUtils.isEmpty(questionImportDTOs)){
            return "导入结束，请确保填写了题目数据";
        }
        //2. 服务降级结构,且将questionImportDTO转换成question
        int successCount = 0;
        for(QuestionImportDTO questionImport :questionImportDTOs){
            try{
                Question question = convertImportToQuestion(questionImport);
                //3. 保存题目
                addQuestion(question);
                successCount++;
            }catch(Exception e){
                log.error("题目{}导入失败", questionImport.getTitle(),e);
            }
        }

        //4. 结果分析
        return String.format("导入结束，共%d条数据，成功导入%d道题目", questionImportDTOs.size(), successCount);

    }

    /**
     * 增加题目热度分数
     */
    @Async// 异步执行
    private void incrementHotScore(Long questionId){
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Double score = zSetOperations.incrementScore(HOT_QUESTION_KEY, questionId, 1);
//        System.out.println("questionId=" + questionId + ", score=" + score);
    }

    /**
     * 设置题目答案和选项
     * @param questionList
     */
    private void setAnswerAndChoice(List<Question> questionList){
        if(CollectionUtils.isEmpty(questionList)){
            return;
        }
        //1. 查询答案表，建立题目和答案的关联map
        List<Long> questionIds =questionList.stream().map(Question::getId).toList();
        //查询答案表
        LambdaQueryWrapper<QuestionAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.in(QuestionAnswer::getQuestionId,questionIds);
        List<QuestionAnswer> answers = questionAnswerMapper.selectList(answerWrapper);
        //建立题目id和答案的映射map
        Map<Long, QuestionAnswer> answerMap = answers.stream()
                .collect(Collectors.toMap(QuestionAnswer::getQuestionId, answer -> answer));

        //2. 查询选项表，建立题目和选项的关联map
        LambdaQueryWrapper<QuestionChoice> choiceWrapper = new LambdaQueryWrapper<>();
        choiceWrapper.in(QuestionChoice::getQuestionId,questionIds);
        List<QuestionChoice> choices = questionChoiceMapper.selectList(choiceWrapper);
        Map<Long, List<QuestionChoice>> choiceMap = choices.stream()
                .collect(Collectors.groupingBy(QuestionChoice::getQuestionId));

        //3. 遍历题目列表，根据题目id获取题目对应的答案和选项
        questionList.forEach(question->{
            Long questionId = question.getId();
            //题目设置答案
            question.setAnswer(answerMap.get(questionId));

            //选择题按顺序设置选项
            if("CHOICE".equals(question.getType())){
                List<QuestionChoice> choiceList = choiceMap.get(questionId);
                if(!CollectionUtils.isEmpty(choiceList)){
                    choiceList.sort(Comparator.comparing(QuestionChoice::getSort));
                    question.setChoices(choiceList);
                }
            }
        });
    }


    /**
     * 将QuestionImportDTO转换成Question对象
     * @param questionImport
     * @return
     */
    private Question convertImportToQuestion(QuestionImportDTO questionImport){
        Question question = new Question();
        //1. 普通属性复制
        BeanUtils.copyProperties(questionImport,question);
        //2. 如果题型是选择题，则需要处理选项
        if("CHOICE".equals(question.getType())){

            List<QuestionImportDTO.ChoiceImportDTO> choiceDTOs= questionImport.getChoices();
            if(!choiceDTOs.isEmpty()){
                List<QuestionChoice> choiceList = new ArrayList<>(choiceDTOs.size());
                for(int i=0;i<choiceDTOs.size();i++){
                    QuestionImportDTO.ChoiceImportDTO choiceImportDTO = choiceDTOs.get(i);
                    QuestionChoice choice = new QuestionChoice();
                    choice.setContent(choiceImportDTO.getContent());
                    choice.setIsCorrect(choiceImportDTO.getIsCorrect());
                    choice.setSort(i);

                    choiceList.add(choice);
                }
                question.setChoices(choiceList);
            }
        }
        //3. 答案处理
        //question里面的answer是QuestionAnswer类型
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setKeywords(questionImport.getKeywords());
        //如果是判断题，答案需要转换成大写
        String answer = questionImport.getAnswer();
        if("JUDGE".equals(question.getType())){
            questionAnswer.setAnswer(answer==null?"":answer.toUpperCase());
        }else{
            questionAnswer.setAnswer(questionImport.getAnswer());
        }
        question.setAnswer(questionAnswer);

        return question;
    }
}
