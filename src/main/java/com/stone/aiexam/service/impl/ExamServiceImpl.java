package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.common.StoneConstant;
import com.stone.aiexam.dto.SubmitAnswerDTO;
import com.stone.aiexam.entity.AnswerRecord;
import com.stone.aiexam.entity.ExamRecord;
import com.stone.aiexam.entity.Paper;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.AnswerRecordMapper;
import com.stone.aiexam.mapper.ExamRecordMapper;
import com.stone.aiexam.dto.AiGradingResult;
import com.stone.aiexam.service.AiService;
import com.stone.aiexam.service.AnswerRecordService;
import com.stone.aiexam.service.ExamService;
import com.stone.aiexam.service.PaperService;
import com.stone.aiexam.vo.ExamRankingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExamServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamService {

    @Autowired
    private PaperService paperService;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private AnswerRecordService answerRecordService;

    @Autowired
    private AiService aiService;


    @Override
    public ExamRecord startExam(Long paperId, String studentName) {
        // 1. 校验：该考生是否还有在进行中的考试
        LambdaQueryWrapper<ExamRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamRecord::getStudentName, studentName);
        queryWrapper.eq(ExamRecord::getExamId, paperId);
        queryWrapper.eq(ExamRecord::getStatus, "进行中");
        ExamRecord examRecord = getOne(queryWrapper);
        if (examRecord != null) {
            return examRecord;
        }

        // 2. 保存考试记录
        examRecord = ExamRecord.builder()
                .examId(paperId.intValue())
                .studentName(studentName)
                .startTime(LocalDateTime.now())
                .status("进行中")
                .windowSwitches(0)
                .build();
        save(examRecord);
        return examRecord;
    }

    @Override
    public List<ExamRecord> listByStudentName(String studentName) {
        return list(new LambdaQueryWrapper<ExamRecord>()
                .eq(ExamRecord::getStudentName, studentName)
                .orderByDesc(ExamRecord::getCreateTime));
    }

    /**
     * 根据id获取考试记录
     * @param id
     * @return
     */
    @Override
    public ExamRecord getExamRecordById(Long id) {
        //1. 根据id获取考试记录基本信息
        ExamRecord examRecord = getById(id);
        if(ObjectUtils.isEmpty(examRecord)){
            throw new BusinessException("考试记录不存在/被删除，请重新开始考试");
        }
        //2. 获取考试的试卷信息
        Paper paper = paperService.getDetailById(examRecord.getExamId().longValue());
        if(ObjectUtils.isEmpty(paper)){
            throw new BusinessException("当前考试记录的试卷不存在/被删除");
        }
        examRecord.setPaper(paper);
        //3. 获取作答记录信息,且让作答记录和试卷题目顺序一致
        LambdaQueryWrapper<AnswerRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerRecord::getExamRecordId,id);
        List<AnswerRecord> answerRecords = answerRecordMapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(answerRecords)){
            //a.建立题目id和顺序的映射
            Map<Integer,Integer> questionOrder=new HashMap<>();
            List<Question> questions = paper.getQuestions();
            for(int i=0;i<questions.size();i++){
                questionOrder.put(questions.get(i).getId().intValue(),i);
            }
            //b. 根据题目id和顺序的映射，对作答记录进行排序
            answerRecords.sort(Comparator.comparingInt(
                    ar->questionOrder.getOrDefault(ar.getQuestionId(),Integer.MAX_VALUE)
            ));
        }
        examRecord.setAnswerRecords(answerRecords);
        return examRecord;
    }

    /**
     * 提交考试
     * @param examRecordId
     * @param records
     */
    @Override
    public void submitExam(Integer examRecordId, List<SubmitAnswerDTO> records) {
        //1. 保存作答记录
        if(!CollectionUtils.isEmpty(records)){
            List<AnswerRecord> answerRecordList = records.stream()
                    .map(r -> new AnswerRecord(examRecordId, r.getQuestionId(), r.getUserAnswer()))
                    .toList();
            answerRecordService.saveBatch(answerRecordList);
        }
        //2. 修改考试记录
        ExamRecord examRecord = getById(examRecordId);
        if(ObjectUtils.isEmpty(examRecord)){
            throw new BusinessException("考试记录不存在");
        }
        examRecord.setStatus(StoneConstant.EXAM_STATUS_FINISH);
        examRecord.setEndTime(LocalDateTime.now());

        updateById(examRecord);
        //3. ai智能批阅
        autoGradeExam(examRecordId);
    }

    /**
     * 智能批阅
     * @param examRecordId
     * @return
     */
    @Override
    public ExamRecord autoGradeExam(Integer examRecordId) {
        //1. 获取考试记录
        ExamRecord examRecord = getExamRecordById(examRecordId.longValue());
        //a. 校验考试记录
        if(ObjectUtils.isEmpty(examRecord)){
            throw new BusinessException("考试记录不存在");
        }
        //b. 校验试卷信息
        Paper paper = examRecord.getPaper();
        if(ObjectUtils.isEmpty(paper)){
            examRecord.setStatus(StoneConstant.EXAM_STATUS_GRADED);
            examRecord.setSummary("试卷已被删除，无法判定成绩");
            throw new BusinessException("试卷已被删除，无法判定成绩");
        }
        //c. 作答记录为空，直接0分，结束判卷
        List<AnswerRecord> answerRecords = examRecord.getAnswerRecords();
        if(CollectionUtils.isEmpty(answerRecords)){
            examRecord.setStatus(StoneConstant.EXAM_STATUS_GRADED);
            examRecord.setScore(0);
            examRecord.setSummary("为作答，成绩为0");
            updateById(examRecord);
            return examRecord;
        }
        //2. 进行判卷，计算成绩，正确题数，作答记录状态修改
        int totalScore = 0;
        int correctCount = 0;

        //建立题目id和题目的映射，方便后续直接获取题目信息
        Map<Long, Question> questionMap = paper.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        for(AnswerRecord answerRecord: answerRecords){
            try{
                Question question = questionMap.get(answerRecord.getQuestionId().longValue());
                String providedAnswer = question.getAnswer().getAnswer();
                String userAnswer = answerRecord.getUserAnswer();

                if(StoneConstant.QUESTION_TYPE_JUDGE.equals(question.getType())){
                    userAnswer = transJudgeAnswer(userAnswer);
                }


                if(StoneConstant.QUESTION_TYPE_TEXT.equals(question.getType())){
                    //a.是简答题，ai智能批阅
                    int maxQScore = question.getPaperScore() != null ? question.getPaperScore().intValue() : 0;
                    AiGradingResult gr = aiService.gradeTextAnswer(question, userAnswer, maxQScore);
                    answerRecord.setScore(gr.getScore());
                    answerRecord.setAiCorrection(gr.getFeedback() + "\n" + gr.getReason());
                    // 根据得分推算正确性
                    if (gr.getScore() >= maxQScore) {
                        answerRecord.setIsCorrect(StoneConstant.ANSWER_STATUS_TRUE);
                    } else if (gr.getScore() > 0) {
                        answerRecord.setIsCorrect(StoneConstant.ANSWER_STATUS_PARTLY_CORRECT);
                    } else {
                        answerRecord.setIsCorrect(StoneConstant.ANSWER_STATUS_FALSE);
                    }
                }else{
                    //b.选择/判断，程序自动比对
                    if(providedAnswer.equalsIgnoreCase(userAnswer)){
                        answerRecord.setIsCorrect(StoneConstant.ANSWER_STATUS_TRUE);
                        answerRecord.setScore(question.getPaperScore().intValue());
                        ;
                    }else{
                        answerRecord.setIsCorrect(StoneConstant.ANSWER_STATUS_FALSE);
                        answerRecord.setScore(0);
                    }
                }
            }catch(Exception e){
                log.error("批阅题目异常, answerRecordId={}, questionId={}", answerRecord.getId(), answerRecord.getQuestionId(), e);
                answerRecord.setIsCorrect(StoneConstant.ANSWER_STATUS_FALSE);
                answerRecord.setScore(0);
                answerRecord.setAiCorrection("系统判题异常");
            }
            //c. 累加分数，统计正确题数
            totalScore += answerRecord.getScore();
            if(Integer.valueOf(StoneConstant.ANSWER_STATUS_TRUE).equals(answerRecord.getIsCorrect())){
                correctCount++;
            }
        }
        answerRecordService.updateBatchById(answerRecords);

        //3. 更改考试记录信息
        //a. 调AI生成总结（纯文本，对齐教程）
        try {
            String summary = aiService.generateExamSummary(
                    examRecord.getStudentName(),
                    paper.getName(),
                    totalScore,
                    paper.getTotalScore() != null ? paper.getTotalScore().intValue() : 0,
                    answerRecords.size(),
                    correctCount);
            examRecord.setSummary(summary);
        } catch (Exception e) {
            examRecord.setSummary("AI总结生成失败，请查看各题批改详情");
        }
        examRecord.setScore(totalScore);
        examRecord.setStatus(StoneConstant.EXAM_STATUS_GRADED);
        updateById(examRecord);

        return examRecord;
    }

    /**
     * 分页查询考试记录
     * @param examRecordPage
     * @param studentName
     * @param status
     * @param startDate
     * @param endDate
     */
    @Override
    public void pageQueryExamRecord(Page<ExamRecord> examRecordPage, String studentName, Integer status, LocalDate startDate, LocalDate endDate) {
        //1. 查询考试记录主体
        LambdaQueryWrapper<ExamRecord> queryWrapper = new LambdaQueryWrapper<>();
        if(!ObjectUtils.isEmpty(status)){
            String statusStr = switch(status){
                case 0 -> StoneConstant.EXAM_STATUS_PROGRESS;
                case 1 -> StoneConstant.EXAM_STATUS_FINISH;
                case 2 -> StoneConstant.EXAM_STATUS_GRADED;
                default -> null;
            };
            queryWrapper.eq(StringUtils.hasText(statusStr), ExamRecord::getStatus, statusStr);
        }

        LocalDateTime startTime = startDate!=null? startDate.atStartOfDay():null;
        LocalDateTime endTime = endDate!=null? endDate.atTime(LocalTime.MAX):null;

        queryWrapper.like(StringUtils.hasText(studentName), ExamRecord::getStudentName, studentName)
                .ge(startTime!=null,ExamRecord::getStartTime,startTime)
                .le(endTime!=null,ExamRecord::getEndTime,endTime);
        page(examRecordPage,queryWrapper);

        //2. 查询对应试卷信息（1对1）
        List<Integer> paperIds = examRecordPage.getRecords().stream().map(ExamRecord::getExamId).toList();
        if(CollectionUtils.isEmpty(paperIds)){
            return;//没有试卷ID，结束
        }
        List<Paper> papers = paperService.listByIds(paperIds);
        Map<Long, Paper> paperMap = papers.stream().collect(Collectors.toMap(Paper::getId, p -> p));

        examRecordPage.getRecords().forEach(examRecord->
                examRecord.setPaper(paperMap.get(examRecord.getExamId().longValue())));
    }

    /**
     * 根据id删除考试记录
     * @param id
     */
    @Override
    public void deleteExamRecordById(Long id) {
        //1. 校验，如果考试在进行中，不允许删除
        ExamRecord examRecord = getById(id);
        if(ObjectUtils.isEmpty(examRecord)){
            return;
        }

        if(StoneConstant.EXAM_STATUS_PROGRESS.equals(examRecord.getStatus())){
            throw new BusinessException("考试进行中，不允许删除");
        }
        //2. 删除考试记录

        removeById(id);
        //3. 删除子数据：答题记录
        LambdaQueryWrapper<AnswerRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerRecord::getExamRecordId,id);
        answerRecordService.remove(queryWrapper);
    }

    /**
     * 考试排行榜
     * @param paperId
     * @param limit
     * @return
     */
    @Override
    public List<ExamRankingVO> rank(Integer paperId, Integer limit) {
        //分析：查询两张表，一个考试记录表，一个试卷表;
        //查询考试记录表, 而且必须是已批阅的试卷
        return baseMapper.rank(paperId,limit);
    }

    /**
     * 转换判断题答案
     * @param answer
     * @return
     */
    private String transJudgeAnswer(String answer){
        if(!StringUtils.hasText(answer)){
            return "";
        }
        answer=answer.trim().toUpperCase();
        return switch (answer) {
            case "T", "TRUE", "正确", "对" -> "TRUE";
            case "F", "FALSE", "错误", "错", "不对" -> "FALSE";
            default -> answer;
        };
    }
}
