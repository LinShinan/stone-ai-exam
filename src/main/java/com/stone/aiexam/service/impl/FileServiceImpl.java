package com.stone.aiexam.service.impl;

import com.stone.aiexam.service.FileService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public FileInfo upload(MultipartFile file, String path) {
        String datePath = new SimpleDateFormat("yyyy/MM").format(new Date());
        return fileStorageService.of(file)
                .setPath(path + "/" + datePath + "/")
                .upload();
    }

    @Override
    public FileInfo uploadImage(MultipartFile file, String path) {
        String datePath = new SimpleDateFormat("yyyy/MM").format(new Date());
        return fileStorageService.of(file)
                .setPath(path + "/" + datePath + "/")
                .image(img -> img.size(1000, 1000))
                .thumbnail(th -> th.size(200, 200))
                .upload();
    }
}