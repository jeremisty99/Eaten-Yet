package com.wjy.service;

import com.wjy.dto.CategoryDTO;
import com.wjy.dto.CategoryPageQueryDTO;
import com.wjy.dto.EmployeePageQueryDTO;
import com.wjy.entity.Category;
import com.wjy.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 修改分类状态
     *
     * @param status
     * @param id
     * @return
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    List<Category> getByType(Integer type);

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    void deleteById(Long id);

    /**
     * 编辑分类信息
     *
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);
}
