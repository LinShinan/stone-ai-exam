package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;


public interface BannerService extends IService<Banner> {

    /**
     * 上传轮播图图片
     * @param file
     * @return
     */
    String uploadBannerImage(MultipartFile file);
}
