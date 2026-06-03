package com.stone.aiexam.controller.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Banner;
import com.stone.aiexam.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="公共端-轮播图")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/common/banners")
public class CommonBannerController {

    @Autowired
    private BannerService bannerService;

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
}
