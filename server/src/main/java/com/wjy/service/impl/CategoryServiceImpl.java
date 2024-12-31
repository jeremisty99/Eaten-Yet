package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjy.constant.StatusConstant;
import com.wjy.dto.CategoryDTO;
import com.wjy.dto.CategoryPageQueryDTO;
import com.wjy.entity.Category;
import com.wjy.mapper.CategoryMapper;
import com.wjy.result.PageResult;
import com.wjy.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        IPage<Category> page = new Page<>(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        String name = categoryPageQueryDTO.getName() == null ? "" : categoryPageQueryDTO.getName();
        queryWrapper.like("name", name);
        categoryMapper.selectPage(page, queryWrapper);
        long total = page.getTotal();
        List<Category> categories = page.getRecords();
        System.out.println("当前页数据：" + categories);
        return new PageResult(total, categories);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @Override
    public void add(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .status(StatusConstant.ENABLE).build();
        categoryMapper.insert(category);
    }

    /**
     * 修改分类状态
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        UpdateWrapper<Category> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", status);
        categoryMapper.update(null, updateWrapper);
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> getByType(Integer type) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        if(type != null) queryWrapper.eq("type", type);
        return categoryMapper.selectList(queryWrapper);
    }

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @Override
    public void deleteById(Long id) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        categoryMapper.delete(queryWrapper);
    }

    /**
     * 编辑员工信息
     *
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.updateById(category);
    }
}
