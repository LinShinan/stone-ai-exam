package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.dto.StartExamDTO;
import com.stone.aiexam.dto.SubmitAnswerDTO;
import com.stone.aiexam.entity.ExamRecord;

import java.util.List;

public interface ExamService extends IService<ExamRecord> {
    /**
     * 开始考试
     * @param startExamDTO
     * @return
     */
    ExamRecord startExam(StartExamDTO startExamDTO);

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
}
