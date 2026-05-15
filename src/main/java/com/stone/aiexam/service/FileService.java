package com.stone.aiexam.service;

import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传文件
     * @param file
     * @param path
     * @return
     */
    FileInfo upload(MultipartFile file, String path);

    /**
     * 上传图片
     * @param file
     * @param path
     * @return
     */
    FileInfo uploadImage(MultipartFile file, String path);
}