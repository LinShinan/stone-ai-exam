package com.stone.aiexam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stone.aiexam.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {


    /**
     * 获取题目数量统计
     * @return {category_id:xxx,count:xxx}
     */
    @Select("select category_id,count(*) as count from questions where is_deleted =0 group by category_id")
    List<Map<String,Long>> getQuestionCountMap();
}
