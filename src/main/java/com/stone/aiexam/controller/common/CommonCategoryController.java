package com.stone.aiexam.controller.common;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Category;
import com.stone.aiexam.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "公共端-题目分类")
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/common/categories")
public class CommonCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取题目分类和对应题目数量
     * @return
     */
    @Operation(summary="获取题目分类和对应题目数量")
    @GetMapping
    public Result<List<Category>> getCategoryList(){
        List<Category> list = categoryService.getCategoryList();
        log.info("getCategoryList: {}", list);
        return Result.success(list);
    }

    /**
     * 获取题目分类树结构
     * @return
     */
    @Operation(summary="获取题目分类树结构")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTreeList(){
        List<Category> list = categoryService.getCategoryTreeList();
        log.info("getCategoryTreeList: {}", list);
        return Result.success(list);
    }
}
