package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wjy.constant.MessageConstant;
import com.wjy.constant.StatusConstant;
import com.wjy.dto.DishDTO;
import com.wjy.dto.DishPageQueryDTO;
import com.wjy.entity.Category;
import com.wjy.entity.Dish;
import com.wjy.entity.DishFlavor;
import com.wjy.entity.SetmealDish;
import com.wjy.exception.DeletionNotAllowedException;
import com.wjy.mapper.CategoryMapper;
import com.wjy.mapper.DishFlavorMapper;
import com.wjy.mapper.DishMapper;
import com.wjy.mapper.SetmealDishMapper;
import com.wjy.result.PageResult;
import com.wjy.service.DishService;
import com.wjy.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @Transactional // 多表操作 保持数据一致性
    @Override
    public void addWithFlavor(DishDTO dishDTO) {
        Dish dish = Dish.builder().build();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.ENABLE);
        dishMapper.insert(dish);
        // Mybatis Plus自带主键返回
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insert(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        IPage<DishVO> page = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        String name = dishPageQueryDTO.getName() == null ? "" : dishPageQueryDTO.getName();

        MPJLambdaWrapper<Dish> mpjLambdaWrapper = new MPJLambdaWrapper<Dish>()
                .selectAll(Dish.class).selectAs(Category::getName, "categoryName")
                .leftJoin(Category.class, Category::getId, Dish::getCategoryId)
                .like(Dish::getName, name);
        if (dishPageQueryDTO.getCategoryId() != null)
            mpjLambdaWrapper.eq(Category::getId, dishPageQueryDTO.getCategoryId());
        if (dishPageQueryDTO.getStatus() != null) mpjLambdaWrapper.eq(Dish::getStatus, dishPageQueryDTO.getStatus());
        dishMapper.selectJoinPage(page, DishVO.class, mpjLambdaWrapper);
        long total = page.getTotal();
        List<DishVO> dishVOS = page.getRecords();
        log.debug("当前页数据：{}", dishVOS);
        return new PageResult(total, dishVOS);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
        // 是否有起售菜品
        for (Long id : ids) {
            QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            Dish dish = dishMapper.selectOne(queryWrapper);
            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(String.format("菜品%s的状态为起售," + MessageConstant.DISH_ON_SALE, dish.getName()));
            }
        }
        // 是否有套餐关联
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("dish_id", ids);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(queryWrapper);
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            StringBuilder dishesResult = new StringBuilder();
            for (SetmealDish setmealDish : setmealDishes) {
                dishesResult.append(setmealDish.getName()).append(",");
            }
            throw new DeletionNotAllowedException(String.format(MessageConstant.DISH_BE_RELATED_BY_SETMEAL, dishesResult));
        }
        // 可以执行删除菜品
        dishMapper.deleteByIds(ids);
        // 删除对应口味
        QueryWrapper<DishFlavor> deleteQueryWrapper = new QueryWrapper<>();
        deleteQueryWrapper.in("dish_id", ids);
        dishFlavorMapper.delete(deleteQueryWrapper);
    }

    /**
     * 修改菜品状态
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", status);
        dishMapper.update(null, updateWrapper);
    }

    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.selectOne(new QueryWrapper<Dish>().eq("id", id));
        List<DishFlavor> flavors = dishFlavorMapper.selectList(new QueryWrapper<DishFlavor>().eq("dish_id", id));
        Category category = categoryMapper.selectById(dish.getCategoryId());
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        dishVO.setCategoryName(category.getName());
        return dishVO;
    }

    /**
     * 编辑菜品信息
     *
     * @param dishDTO
     * @return
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        Long id = dishDTO.getId();
        // 原来的口味和新的口味的行数据量可能不一样，不能直接更新，只能批量删除再批量插入
        dishFlavorMapper.delete(new QueryWrapper<DishFlavor>().eq("dish_id", id));
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);
            }
            dishFlavorMapper.insert(flavors);
        }
    }

    /**
     * 根据参数动态查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getList(Long categoryId, String name) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        if (name != null) {
            queryWrapper.like("name", name);
        }
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        return dishMapper.selectList(queryWrapper);
    }

    @Override
    public List<DishVO> getListWithFlavor(Long categoryId, String name) {
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        if (name != null) {
            queryWrapper.like("name", name);
        }
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishVO> dishVOS = new ArrayList<>();
        for (Dish dish : dishes) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(dishFlavorMapper.selectList(new QueryWrapper<DishFlavor>().eq("dish_id", dish.getId())));
            dishVOS.add(dishVO);
        }
        return dishVOS;
    }


}
