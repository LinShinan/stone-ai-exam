package com.stone.aiexam.controller;


import com.stone.aiexam.common.Result;
import com.stone.aiexam.entity.Category;
import com.stone.aiexam.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@Tag(name = "题目分类管理")
@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {


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

    /**
     * 新增题目分类
     * @param category
     * @return
     */
    @Operation(summary="新增题目分类")
    @PostMapping
    public Result<Void> addCategory(@RequestBody Category category){
        categoryService.addCategory(category);
        log.info("新增id={}的分类", category.getId());
        return Result.success();
    }

    /**
     * 更新题目分类
     * @param category
     * @return
     */
    @Operation(summary="更新题目分类")
    @PutMapping
    public Result<Void> updateCategory(@RequestBody Category category){
        categoryService.updateCategory(category);
        log.info("更新id={}的分类", category.getId());
        return Result.success();
    }


    /**
     * 删除题目分类
     * @param id
     * @return
     */
    @Operation(summary="删除题目分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        log.info("删除id={}的分类", id);
        return Result.success();
    }


}
