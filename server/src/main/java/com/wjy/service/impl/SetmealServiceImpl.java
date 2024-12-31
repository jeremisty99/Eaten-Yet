package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wjy.constant.MessageConstant;
import com.wjy.constant.StatusConstant;
import com.wjy.dto.SetmealDTO;
import com.wjy.dto.SetmealPageQueryDTO;
import com.wjy.entity.*;
import com.wjy.exception.DeletionNotAllowedException;
import com.wjy.mapper.DishMapper;
import com.wjy.mapper.SetmealDishMapper;
import com.wjy.mapper.SetmealMapper;
import com.wjy.result.PageResult;
import com.wjy.service.SetmealService;
import com.wjy.vo.DishItemVO;
import com.wjy.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = Setmeal.builder().build();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.ENABLE);
        setmealMapper.insert(setmeal);
        // Mybatis Plus自带主键返回
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insert(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        IPage<SetmealVO> page = new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        String name = setmealPageQueryDTO.getName() == null ? "" : setmealPageQueryDTO.getName();

        MPJLambdaWrapper<Setmeal> mpjLambdaWrapper = new MPJLambdaWrapper<Setmeal>()
                .selectAll(Setmeal.class).selectAs(Category::getName, "categoryName")
                .leftJoin(Category.class, Category::getId, Setmeal::getCategoryId)
                .like(Setmeal::getName, name);
        if (setmealPageQueryDTO.getCategoryId() != null)
            mpjLambdaWrapper.eq(Category::getId, setmealPageQueryDTO.getCategoryId());
        if (setmealPageQueryDTO.getStatus() != null)
            mpjLambdaWrapper.eq(Setmeal::getStatus, setmealPageQueryDTO.getStatus());
        setmealMapper.selectJoinPage(page, SetmealVO.class, mpjLambdaWrapper);
        long total = page.getTotal();
        List<SetmealVO> setmealVOS = page.getRecords();
        log.debug("当前页数据：{}", setmealVOS);
        return new PageResult(total, setmealVOS);
    }

    /**
     * 套餐批量删除
     *
     * @param ids
     */
    public void delete(List<Long> ids) {
        // 是否有起售套餐
        for (Long id : ids) {
            QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            Setmeal setmeal = setmealMapper.selectOne(queryWrapper);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(String.format(MessageConstant.SETMEAL_ON_SALE, setmeal.getName()));
            }
        }
        // 删除套餐
        setmealMapper.deleteByIds(ids);
        // 删除套餐内菜品关联表数据
        for (Long id : ids) {
            setmealDishMapper.delete(new QueryWrapper<SetmealDish>().eq("setmeal_id", id));
        }
    }

    /**
     * 修改套餐状态
     *
     * @param status
     * @param id
     * @return
     */
    public void updateStatus(Integer status, Long id) {
        // 停售无需判断 启售需要判断套餐内菜品是否全部启售
        UpdateWrapper<Setmeal> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", status);
        if (status.equals(StatusConstant.ENABLE)) {
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(new QueryWrapper<SetmealDish>().eq("setmeal_id", id));
            for (SetmealDish setmealDish : setmealDishes) {
                Dish dish = dishMapper.selectById(setmealDish.getDishId());
                if (dish.getStatus().equals(StatusConstant.DISABLE)) {
                    throw new DeletionNotAllowedException(String.format(MessageConstant.SETMEAL_ENABLE_FAILED, dish.getName()));
                }
            }
        }
        setmealMapper.update(null, updateWrapper);
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.selectOne(new QueryWrapper<Setmeal>().eq("id", id));
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(new QueryWrapper<SetmealDish>().eq("setmeal_id", id));
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 编辑套餐信息
     *
     * @param setmealDTO
     * @return
     */
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateById(setmeal);
        Long id = setmealDTO.getId();
        // 原来的菜品和新的菜品的行数据量可能不一样，不能直接更新，只能批量删除再批量插入
        setmealDishMapper.delete(new QueryWrapper<SetmealDish>().eq("setmeal_id", id));
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(id);
            }
            setmealDishMapper.insert(setmealDishes);
        }
    }

    /**
     * 根据分类id查询套餐
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> getListWithFlavor(Long categoryId) {
        return setmealMapper.selectList(new QueryWrapper<Setmeal>().eq("category_id", categoryId));
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        MPJLambdaWrapper<SetmealDish> mpjLambdaWrapper = new MPJLambdaWrapper<SetmealDish>()
                .select(SetmealDish::getName, SetmealDish::getCopies)
                .select(Dish::getImage, Dish::getDescription)
                .leftJoin(Dish.class, Dish::getId, SetmealDish::getDishId);
        List<DishItemVO> dishItemVOS = setmealDishMapper.selectJoinList(DishItemVO.class, mpjLambdaWrapper.eq(SetmealDish::getSetmealId, id));
        log.info("查询到的菜品数据：{}", dishItemVOS);
        return dishItemVOS;
    }
}
