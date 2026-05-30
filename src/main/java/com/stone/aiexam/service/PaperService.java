package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.dto.PaperDTO;
import com.stone.aiexam.dto.SmartPaperDTO;
import com.stone.aiexam.entity.Paper;

public interface PaperService extends IService<Paper> {

    /**
     * 创建试卷
     * @param paperDTO
     */
    void addPaper(PaperDTO paperDTO);

    /**
     * 智能组卷
     * @param smartPaperDTO
     */
    void createSmartPaper(SmartPaperDTO smartPaperDTO);

    /**
     * 更新试卷
     * @param id
     * @param paperDTO
     */
    void updatePaper(Long id, PaperDTO paperDTO);

    /**
     * 删除试卷
     * @param id
     */
    void deletePaperById(Long id);

    /**
     * 根据id获取试卷详情
     * @param id
     * @return
     */
    Paper getDetailById(Long id);
}
