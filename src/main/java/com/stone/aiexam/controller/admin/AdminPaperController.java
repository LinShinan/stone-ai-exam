package com.stone.aiexam.controller.admin;

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

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "管理端-试卷")
@RequestMapping("/api/admin/papers")
public class AdminPaperController {

    @Autowired
    private PaperService paperService;

    @Operation(summary = "试卷列表")
    @GetMapping("/list")
    public Result<List<Paper>> list(String name, String status) {
        LambdaQueryWrapper<Paper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(name), Paper::getName, name)
                .eq(StringUtils.hasText(status), Paper::getStatus, status);
        List<Paper> list = paperService.list(queryWrapper);
        log.info("试卷列表: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "试卷详情")
    @GetMapping("/{id}")
    public Result<Paper> getById(@PathVariable Long id) {
        Paper paper = paperService.getDetailById(id);
        log.info("试卷详情: {}", paper);
        return Result.success(paper);
    }

    @Operation(summary = "创建试卷")
    @PostMapping
    public Result<Void> add(@RequestBody PaperDTO paperDTO) {
        paperService.addPaper(paperDTO);
        log.info("创建试卷: {}", paperDTO);
        return Result.success();
    }

    @Operation(summary = "智能组卷")
    @PostMapping("/smart")
    public Result<Void> createSmart(@RequestBody SmartPaperDTO dto) {
        paperService.createSmartPaper(dto);
        log.info("智能组卷: {}", dto);
        return Result.success();
    }

    @Operation(summary = "更新试卷")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PaperDTO paperDTO) {
        paperService.updatePaper(id, paperDTO);
        log.info("更新试卷: {}", paperDTO);
        return Result.success();
    }

    @Operation(summary = "更新试卷状态")
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        LambdaUpdateWrapper<Paper> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Paper::getId, id).set(Paper::getStatus, status);
        paperService.update(updateWrapper);
        log.info("更新试卷id={}状态为: {}", id, status);
        return Result.success();
    }

    @Operation(summary = "删除试卷")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paperService.deletePaperById(id);
        log.info("删除试卷id={}", id);
        return Result.success();
    }
}