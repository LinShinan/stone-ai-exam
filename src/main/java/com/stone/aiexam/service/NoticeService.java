package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.entity.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice> {

    /**
     * 获取最新的启用的公告
     * @param limit 条数
     * @return
     */
    List<Notice> getLatestActiveNoticeList(int limit);

    /**
     * 获取所有启用的公告
     * @return
     */
    List<Notice> getActiveNoticeList();

    /**
     * 启用或禁用公告
     * @param id
     * @param isActive
     */
    void enableOrDisableNotice(Long id, boolean isActive);
}
