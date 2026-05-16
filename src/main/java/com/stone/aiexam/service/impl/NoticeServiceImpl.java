package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.entity.Notice;
import com.stone.aiexam.mapper.NoticeMapper;
import com.stone.aiexam.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 获取最新的启用的公告
     * @param limit 条数
     * @return
     */
    @Override
    public List<Notice> getLatestActiveNoticeList(int limit) {
        // 1.只查询启用的 2.按创建优先级，时间降序 3.限制条数
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getIsActive,true)
                .orderByDesc(Notice::getPriority)
                .orderByDesc(Notice::getCreateTime)
                .last("LIMIT "+limit);
        return noticeMapper.selectList(queryWrapper);
    }

    /**
     * 获取所有启用的公告
     * @return
     */
    @Override
    public List<Notice> getActiveNoticeList() {
        // 1.只查询启用的 2.按创建优先级，时间降序
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getIsActive,true)
                .orderByDesc(Notice::getPriority)
                .orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectList(queryWrapper);
    }

    /**
     * 启用或禁用公告
     * @param id
     * @param isActive
     */
    @Override
    public void enableOrDisableNotice(Long id, boolean isActive) {
        LambdaUpdateWrapper<Notice> updateWrapper= new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notice::getId,id).set(Notice::getIsActive,isActive);
        noticeMapper.update(updateWrapper);
    }
}
