package com.wjy.service;

import com.wjy.dto.SetmealDTO;
import com.wjy.dto.SetmealPageQueryDTO;
import com.wjy.dto.SetmealDTO;
import com.wjy.dto.SetmealPageQueryDTO;
import com.wjy.entity.Setmeal;
import com.wjy.result.PageResult;
import com.wjy.vo.DishItemVO;
import com.wjy.vo.SetmealVO;
import com.wjy.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService {
    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    void add(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐批量删除
     *
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 修改套餐状态
     *
     * @param status
     * @param id
     * @return
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 编辑套餐信息
     *
     * @param setmealDTO
     * @return
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 根据分类id查询套餐
     *
     * @param categoryId
     * @return
     */
    List<Setmeal> getListWithFlavor(Long categoryId);

    /**
     * 根据套餐id查询菜品
     *
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
