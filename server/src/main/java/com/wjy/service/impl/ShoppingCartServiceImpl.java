package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjy.context.BaseContext;
import com.wjy.dto.ShoppingCartDTO;
import com.wjy.entity.Dish;
import com.wjy.entity.Setmeal;
import com.wjy.entity.ShoppingCart;
import com.wjy.mapper.DishMapper;
import com.wjy.mapper.SetmealMapper;
import com.wjy.mapper.ShoppingCartMapper;
import com.wjy.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前加入到购物车中的商品是否己经存在
        List<ShoppingCart> list = list(shoppingCartDTO);
        if (list != null && !list.isEmpty()) {
            ShoppingCart shoppingCart = list.get(0);
            // 已存在 数量加一
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateById(shoppingCart);
        } else {
            ShoppingCart shoppingCart;
            if (shoppingCartDTO.getDishId() != null) {
                // 添加的是菜品
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart = ShoppingCart.builder()
                        .userId(BaseContext.getCurrentId())
                        .name(dish.getName())
                        .image(dish.getImage())
                        .amount(dish.getPrice())
                        .dishId(dish.getId())
                        .dishFlavor(shoppingCartDTO.getDishFlavor())
                        .number(1)
                        .createTime(LocalDateTime.now())
                        .build();
            } else if (shoppingCartDTO.getSetmealId() != null) {
                // 添加的是套餐
                Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
                shoppingCart = ShoppingCart.builder()
                        .userId(BaseContext.getCurrentId())
                        .name(setmeal.getName())
                        .image(setmeal.getImage())
                        .amount(setmeal.getPrice())
                        .setmealId(setmeal.getId())
                        .number(1)
                        .createTime(LocalDateTime.now())
                        .build();
            } else {
                throw new RuntimeException("添加购物车失败");
            }
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 查询购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    public List<ShoppingCart> list(ShoppingCartDTO shoppingCartDTO) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        if (shoppingCartDTO.getDishId() != null)
            queryWrapper.eq("dish_id", shoppingCartDTO.getDishId());
        if (shoppingCartDTO.getSetmealId() != null)
            queryWrapper.eq("setmeal_id", shoppingCartDTO.getSetmealId());
        if (shoppingCartDTO.getDishFlavor() != null)
            queryWrapper.eq("dish_flavor", shoppingCartDTO.getDishFlavor());
        if (BaseContext.getCurrentId() != null)
            queryWrapper.eq("user_id", BaseContext.getCurrentId());
        return shoppingCartMapper.selectList(queryWrapper);
    }

    @Override
    public void clean() {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", BaseContext.getCurrentId());
        shoppingCartMapper.delete(queryWrapper);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        List<ShoppingCart> list = list(shoppingCartDTO);
        if (list != null && !list.isEmpty()) {
            ShoppingCart shoppingCart = list.get(0);
            if (shoppingCart.getNumber() == 1) {
                shoppingCartMapper.deleteById(shoppingCart);
            } else {
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateById(shoppingCart);
            }
        }
    }
}
