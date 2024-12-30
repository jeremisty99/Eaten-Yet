package com.wjy.service;

import com.wjy.dto.DishDTO;
import com.wjy.dto.DishPageQueryDTO;
import com.wjy.entity.Dish;
import com.wjy.result.PageResult;
import com.wjy.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    void addWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 修改菜品状态
     *
     * @param status
     * @param id
     * @return
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    DishVO getById(Long id);

    /**
     * 编辑菜品信息
     *
     * @param dishDTO
     * @return
     */
    void update(DishDTO dishDTO);

    /**
     * 根据参数动态查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Dish> getList(Long categoryId, String name);

    /**
     * 根据参数动态查询菜品包含口味数据
     * @param categoryId
     * @param name
     * @return
     */
    List<DishVO> getListWithFlavor(Long categoryId, String name);
}
