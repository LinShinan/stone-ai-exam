package com.stone.aiexam.controller;


import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Notice;
import com.stone.aiexam.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@Tag(name = "公告管理")
@Slf4j
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;


    /**
     * 获取所有公告
     * @return
     */
    @Operation(summary = "获取所有公告")
    @GetMapping("/list")
    public Result<List<Notice>> getAllNoticeList(){
        List<Notice> noticeList = noticeService.list();
        log.info("获取所有公告: {}", noticeList);
        return Result.success(noticeList);
    }

    /**
     * 获取最新公告
     * @param limit 条数
     * @return
     */
    @Operation(summary = "获取最新公告")
    @GetMapping("/latest")
    public Result<List<Notice>> getLatestNoticeList(@RequestParam(defaultValue = "5") int limit){
        List<Notice> noticeList = noticeService.getLatestActiveNoticeList(limit);
        log.info("获取最新公告: {}", noticeList);
        return Result.success(noticeList);
    }

    /**
     * 获取所有启用的公告
     * @return
     */
    @Operation(summary = "获取启用的公告")
    @GetMapping("/active")
    public Result<List<Notice>> getActiveNoticeList(){
        List<Notice> noticeList = noticeService.getActiveNoticeList();
        log.info("获取所有启用的公告: {}", noticeList);
        return Result.success(noticeList);
    }

    /**
     * 根据ID获取公告详情
     * @param id
     * @return
     */
    @Operation(summary = "根据ID获取公告详情")
    @GetMapping("/{id}")
    public Result<Notice> getNoticeById(@PathVariable Long id){
        Notice notice = noticeService.getById(id);
        if(notice==null){
            return Result.fail("公告不存在");
        }
        log.info("获取了id={}的公告详情", id);
        return Result.success(notice);
    }

    /**
     * 启用或禁用公告
     * @param id
     * @param isActive
     * @return
     */
    @Operation(summary = "启用或禁用公告")
    @PutMapping("/switch/{id}")
    public Result<Void> enableOrDisableNotice(@PathVariable Long id, @RequestParam boolean isActive){
        noticeService.enableOrDisableNotice(id,isActive);
        log.info("id={}的公告已{}", id, isActive ? "启用" : "禁用");
        return Result.success();
    }

    /**
     * 更新公告
     * @param notice
     * @return
     */
    @Operation(summary = "更新公告")
    @PutMapping("/update")
    public Result<Void> udpateNotice(@RequestBody Notice notice){
        noticeService.updateById(notice);
        log.info("id={}的公告已更新", notice.getId());
        return Result.success();
    }


    /**
     * 添加公告
     * @param notice
     * @return
     */
    @Operation(summary = "添加公告")
    @PostMapping("/add")
    public Result<Void> addNotice(@RequestBody Notice notice){
        noticeService.save(notice);
        log.info("新增id={}的公告", notice.getId());
        return Result.success();
    }

    /**
     * 删除公告
     * @param id
     * @return
     */
    @Operation(summary = "删除公告")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteNoticeById(@PathVariable Long id){
        noticeService.removeById(id);
        log.info("删除id={}的公告", id);
        return Result.success();
    }
}
