package com.stone.aiexam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.PaperDTO;
import com.stone.aiexam.dto.SmartPaperDTO;
import com.stone.aiexam.entity.Paper;
import com.stone.aiexam.service.PaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Slf4j
@Tag(name="试卷管理")
@RestController
@RequestMapping("/api/papers")
public class PaperController {

    @Autowired
    private PaperService paperService;

    /**
     * 获取试卷列表
     * @return
     */
    @Operation(summary = "获取试卷列表")
    @GetMapping("/list")
    public Result<List<Paper>> getPaperList(String name,String status){
        LambdaQueryWrapper<Paper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name),Paper::getName,name)
                .eq(StringUtils.hasText(status),Paper::getStatus,status);

        List<Paper> papers = paperService.list(queryWrapper);
        log.info("getPaperList: {}", papers);
        return Result.success(papers);
    }

    /**
     * 创建试卷
     * @param paperDTO
     * @return
     */
    @Operation(summary="创建试卷")
    @PostMapping
    public Result<Void> addPaper(@RequestBody PaperDTO paperDTO){
        paperService.addPaper(paperDTO);
        log.info("创建试卷: {}", paperDTO);
        return Result.success();
    }

    /**
     * 智能组卷
     * @param smartPaperDTO
     * @return
     */
    @Operation(summary="智能组卷")
    @PostMapping("/smart")
    public Result<Void>  createSmartPaper(@RequestBody SmartPaperDTO smartPaperDTO){
        paperService.createSmartPaper(smartPaperDTO);
        log.info("智能组卷: {}", smartPaperDTO);
        return Result.success();
    }

    /**
     * 更新试卷
     * @param id
     * @param paperDTO
     * @return
     */
    @Operation(summary="更新试卷")
    @PutMapping("/{id}")
    public Result<Void> updatePaper(@PathVariable Long id,@RequestBody PaperDTO paperDTO){
        paperService.updatePaper(id, paperDTO);
        log.info("更新试卷: {}", paperDTO);
        return Result.success();
    }

    /**
     * 更新试卷状态
     * @param id
     * @param status
     * @return
     */
    @Operation(summary="更新试卷状态")
    @PatchMapping("/{id}/status")
    public Result<Void> updatePaperStatus(@PathVariable Long id, @RequestParam String status){
        LambdaUpdateWrapper<Paper> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Paper::getId,id).set(Paper::getStatus,status);
        paperService.update(updateWrapper);
        log.info("更新id={}的试卷状态为: {}", id,status);
        return Result.success();
    }

    /**
     * 删除试卷
     * @param id
     * @return
     */
    @Operation(summary="删除试卷")
    @DeleteMapping("/{id}")
    public Result<Void> deletePaper(@PathVariable Long id){
        paperService.deletePaperById(id);
        log.info("删除id={}的试卷", id);
        return Result.success();
    }

    /**
     * 获取试卷详情
     * @param id
     * @return
     */
    @Operation(summary = "根据id获取试卷详情")
    @GetMapping("/{id}")
    public Result<Paper> getPaperById(@PathVariable Long id){
        Paper paper = paperService.getDetailById(id);
        log.info("查看试卷详情：{}",paper);
        return Result.success(paper);
    }

}
