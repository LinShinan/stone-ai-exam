package com.stone.aiexam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.aiexam.entity.ExamRecord;
import com.stone.aiexam.vo.ExamRankingVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    /**
     * 获取考试排行榜
     * @param paperId
     * @param limit
     * @return
     */
    List<ExamRankingVO> rank(Integer paperId, Integer limit);
}
