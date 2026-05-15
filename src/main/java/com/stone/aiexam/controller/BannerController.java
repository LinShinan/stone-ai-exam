package com.stone.aiexam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stone.aiexam.entity.Banner;
import com.stone.aiexam.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.stone.aiexam.common.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin // 允许跨域
@Tag(name = "轮播图管理")
@Slf4j
@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;


    /**
     * 获取所有轮播图列表
     * @return
     */
    @Operation(summary = "获取所有轮播图列表")
    @GetMapping("/list")
    public Result<List<Banner>> getAllBannerList(){
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Banner::getSortOrder);

        List<Banner> bannerList = bannerService.list(queryWrapper);
        log.info("所有轮播图: {}", bannerList);
        return Result.success(bannerList);
    }


    /**
     * 获取前台（激活的）轮播图列表
     * @return
     */
    @Operation(summary = "获取激活轮播图列表")
    @GetMapping("/active")
    public Result<List<Banner>> getActiveBannerList(){
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Banner::getSortOrder);
        queryWrapper.eq(Banner::getIsActive,true);

        List<Banner> bannerList = bannerService.list(queryWrapper);
        log.info("激活的轮播图: {}", bannerList);
        return Result.success(bannerList);
    }

    /**
     * 启用或禁用轮播图
     * @param id
     * @param isActive
     * @return
     */
    @Operation(summary = "启用或禁用轮播图")
    @PutMapping("/switch/{id}")
    public Result<Void> enableOrDisableBanner(@PathVariable Long id,@RequestParam Boolean isActive){
        LambdaUpdateWrapper<Banner> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Banner::getId, id);
        updateWrapper.set(Banner::getIsActive,isActive);
        bannerService.update(updateWrapper);//数据库中已设置自动更新修改时间，所以此处不需要设置修改时间

        log.info("轮播图{}已{}", id, isActive ? "启用" : "禁用");
        return Result.success();
    }

    /**
     *
     * 删除轮播图
     * @param id
     * @return
     */
    @Operation(summary ="根据ID删除轮播图")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteBannerById(@PathVariable Long id){
        bannerService.removeById(id);
        log.info("id={}的轮播图已删除", id);
        return Result.success();
    }


    /**
     * 根据id获取轮播图详情
     * @param id
     * @return
     */
    @Operation(summary = "根据id获取轮播图详情")
    @GetMapping("/{id}")
    public Result<Banner> getBannerById(@PathVariable Long id){
        Banner banner = bannerService.getById(id);
        log.info("id={}的轮播图详情: {}", id,banner);
        return Result.success(banner);
    }

    /**
     * 上传轮播图图片
     * @param file
     * @return
     */
    @Operation(summary = "上传轮播图图片")
    @PostMapping("/upload-image")
    public Result<String> uploadBannerImage(@RequestParam("file") MultipartFile file){
        String imageUrl = bannerService.uploadBannerImage(file);
        return Result.success("上传成功", imageUrl);
    }

    /**
     * 添加轮播图
     * @param banner
     * @return
     */
    @Operation(summary = "添加轮播图")
    @PostMapping("/add")
    public Result<Void> addBanner(@RequestBody Banner banner){
        bannerService.save(banner);
        log.info("新增id={}的轮播图", banner.getId()); //save() 默认回填自增主键，无需手动查询
        return Result.success();
    }

    /**
     * 更新轮播图
     * @param banner
     * @return
     */
    @Operation(summary = "更新轮播图")
    @PutMapping("/update")
    public Result<Void> updateBanner(@RequestBody Banner banner){
        bannerService.updateById(banner);
        log.info("id={}的轮播图更新成功", banner.getId());
        return Result.success();
    }

}
