package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.entity.Banner;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.BannerMapper;
import com.stone.aiexam.service.BannerService;
import com.stone.aiexam.service.FileService;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Autowired
    private FileService fileService;

    /**
     * 上传轮播图图片
     * @param file
     * @return
     */
    @Override
    public String uploadBannerImage(MultipartFile file) {
        //1. 判断上传文件是否为空，是否为图片
        if(file.isEmpty()){
            throw new BusinessException("不能上传空的图片");
        }
        String type = file.getContentType();
        if(type==null || !type.startsWith("image/")){
            throw new BusinessException("只能上传图片");
        }

        //2. 上传图片，返回imageUrl
        FileInfo fileInfo = fileService.uploadImage(file, "banners");
        return fileInfo.getUrl();
    }
}
