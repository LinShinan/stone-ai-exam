package com.stone.aiexam.controller.common;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.service.ExamService;
import com.stone.aiexam.vo.ExamRankingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "公共端-考试")
@RequestMapping("/api/common/exams")
public class CommonExamController {

    @Autowired
    private ExamService examService;

    @Operation(summary = "考试排行榜")
    @GetMapping("/ranking")
    public Result<List<ExamRankingVO>> ranking(Integer paperId, Integer limit) {
        List<ExamRankingVO> rank = examService.rank(paperId, limit);
        log.info("考试排行榜: {}", rank);
        return Result.success(rank);
    }
}
