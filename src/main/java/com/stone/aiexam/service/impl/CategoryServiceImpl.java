package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.entity.Category;
import com.stone.aiexam.entity.Question;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.CategoryMapper;
import com.stone.aiexam.mapper.QuestionMapper;
import com.stone.aiexam.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 获取分类列表，并设置每个分类的题目数量
     * @return 分类列表
     */
    @Override
    public List<Category> getCategoryList() {
        //1. 查询分类表，得到所有分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);// 按排序字段升序排列
        List<Category> categories = list(queryWrapper);

        //2. 查询题目表，得到分类id和题目数量
        List<Map<String, Long>> questionCountMap = questionMapper.getQuestionCountMap();
        //3. 建立分类id和题目数量关系映射
        Map<Long, Long> countMap = questionCountMap.stream()
                .collect(Collectors.toMap(map -> map.get("category_id"), map -> map.get("count")));

        //4. 遍历分类列表，设置题目数量
        for(Category category: categories){
            category.setCount(countMap.getOrDefault(category.getId(),0L));
        }
        return categories;
    }

    /**
     * 获取分类树列表
     * @return 分类树列表
     */
    @Override
    public List<Category> getCategoryTreeList() {
        //1. 获取设置了题目数量的分类列表
        List<Category> categoryList = getCategoryList();
        //2. 过滤分类列表获得一级分类
        List<Category> parentList = categoryList.stream().filter(c -> c.getParentId() == 0).toList();
        //3. 将子分类按照parentId分组建立映射
        Map<Long, List<Category>> childrenMap = categoryList.stream()
                .filter(c -> c.getParentId() != 0)
                .collect(Collectors.groupingBy(Category::getParentId));
        //4. 为一级分类设置子分类并计算题目数量
        parentList.forEach(parent -> {
            List<Category> childrenList = childrenMap.getOrDefault(parent.getId(), List.of());
            parent.setChildren(childrenList);
            Long childrenCount = childrenList.stream().collect(Collectors.summingLong(Category::getCount));
            parent.setCount(parent.getCount()+childrenCount);
        });

        return parentList;
    }

    /**
     * 添加分类
     * @param category
     */
    @Override
    public void addCategory(Category category) {
        //1. 判断同一父分类下有没有和要添加的分类同名的
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId,category.getParentId())
                .eq(Category::getName,category.getName());
        long count = count(queryWrapper);
        if(count > 0){
            throw new BusinessException("同一父分类下已存在同名分类：" + category.getName());
        }

        //2. 添加分类
        save(category);
    }

    /**
     * 更新分类
     * @param category
     */
    @Override
    public void updateCategory(Category category) {
        //1. 更新不能和除了自己以外的子分类重名
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId,category.getParentId())
                .eq(Category::getName,category.getName())
                .ne(Category::getId,category.getId());// 排除自己
        long count = count(queryWrapper);
        if(count > 0){
            throw new BusinessException("同一父分类下已存在同名分类：" + category.getName());
        }

        //2. 更新分类
        updateById(category);
    }

    /**
     * 删除分类
     * @param id
     */
    @Override
    public void deleteCategory(Long id) {
        //1. 一级分类不能删除
        Category category = getById(id);
        if(category ==null){
            return;
        }
        if(category.getParentId() == 0){
            throw new BusinessException(category.getName()+"为一级分类不能删除");
        }
        //2. 关联题目不能删除
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getCategoryId,id);
        Long count = questionMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException("分类下存在%d个题目不能删除".formatted(count));
        }
        //3. 删除分类
        removeById(category);
    }
}
