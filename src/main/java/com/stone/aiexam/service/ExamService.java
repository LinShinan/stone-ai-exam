package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.dto.SubmitAnswerDTO;
import com.stone.aiexam.entity.ExamRecord;
import com.stone.aiexam.vo.ExamRankingVO;

import java.time.LocalDate;
import java.util.List;

public interface ExamService extends IService<ExamRecord> {
    /**
     * 开始考试
     * @param paperId
     * @param studentName 从 token 解析的学生用户名
     * @return
     */
    ExamRecord startExam(Long paperId, String studentName);

    /**
     * 查询学生的考试记录
     * @param studentName
     * @return
     */
    List<ExamRecord> listByStudentName(String studentName);

    /**
     * 获取考试记录
     * @param id
     * @return
     */
    ExamRecord getExamRecordById(Long id);

    /**
     * 提交考试
     * @param examRecordId
     * @param records
     */
    void submitExam(Integer examRecordId, List<SubmitAnswerDTO> records);

    /**
     * 智能批阅
     * @param examRecordId
     * @return
     */
    ExamRecord autoGradeExam(Integer examRecordId);

    /**
     * 分页查询考试记录
     * @param examRecordPage
     * @param studentName
     * @param status
     * @param startDate
     * @param endDate
     */
    void pageQueryExamRecord(Page<ExamRecord> examRecordPage, String studentName, Integer status, LocalDate startDate,LocalDate endDate);

    /**
     * 根据id删除考试记录
     * @param id
     */
    void deleteExamRecordById(Long id);

    /**
     * 考试排行榜
     * @param paperId
     * @param limit
     * @return
     */
    List<ExamRankingVO> rank(Integer paperId, Integer limit);
}
