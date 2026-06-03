package com.stone.aiexam.controller.admin;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Notice;
import com.stone.aiexam.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "管理端-公告")
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {

    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "公告列表")
    @GetMapping("/list")
    public Result<List<Notice>> list() {
        List<Notice> list = noticeService.list();
        log.info("公告列表: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "新增公告")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.save(notice);
        log.info("新增公告id={}", notice.getId());
        return Result.success();
    }

    @Operation(summary = "更新公告")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        log.info("更新公告id={}", notice.getId());
        return Result.success();
    }

    @Operation(summary = "启用/禁用公告")
    @PutMapping("/switch/{id}")
    public Result<Void> toggle(@PathVariable Long id, @RequestParam boolean isActive) {
        noticeService.enableOrDisableNotice(id, isActive);
        log.info("公告{}已{}", id, isActive ? "启用" : "禁用");
        return Result.success();
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        log.info("删除公告id={}", id);
        return Result.success();
    }
}