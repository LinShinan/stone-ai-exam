package com.stone.aiexam.controller.common;

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
@Tag(name = "公共端-公告")
@RequestMapping("/api/common/notices")
public class CommonNoticeController {

    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "最新公告")
    @GetMapping("/latest")
    public Result<List<Notice>> getLatestList(@RequestParam(defaultValue = "5") int limit) {
        List<Notice> list = noticeService.getLatestActiveNoticeList(limit);
        log.info("最新公告: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "启用公告列表")
    @GetMapping("/active")
    public Result<List<Notice>> getActiveList() {
        List<Notice> list = noticeService.getActiveNoticeList();
        log.info("启用的公告: {}", list);
        return Result.success(list);
    }

    @Operation(summary = "公告详情")
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        if (notice == null) {
            return Result.fail("公告不存在");
        }
        log.info("公告详情id={}", id);
        return Result.success(notice);
    }
}