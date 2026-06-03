package com.stone.aiexam.controller.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Paper;
import com.stone.aiexam.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "公共端-试卷")
@RequestMapping("/api/common/papers")
public class CommonPaperController {

    @Autowired
    private PaperService paperService;

    @Operation(summary = "已发布试卷列表")
    @GetMapping("/list")
    public Result<List<Paper>> publishedList(String name) {
        LambdaQueryWrapper<Paper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Paper::getStatus, "published")
                .like(name != null, Paper::getName, name);
        List<Paper> list = paperService.list(queryWrapper);
        log.info("已发布试卷列表: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "试卷详情")
    @GetMapping("/{id}")
    public Result<Paper> getById(@PathVariable Long id) {
        Paper paper = paperService.getDetailById(id);
        log.info("试卷详情: {}", paper);
        return Result.success(paper);
    }
}