package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取题目分类和对应题目数量
     * @return
     */
    List<Category> getCategoryList();

    /**
     * 获取题目分类树结构
     * @return
     */
    List<Category> getCategoryTreeList();

    /**
     * 新增题目分类
     * @param category
     */
    void addCategory(Category category);

    /**
     * 更新题目分类
     * @param category
     */
    void updateCategory(Category category);

    /**
     * 删除题目分类
     * @param id
     */
    void deleteCategory(Long id);
}
