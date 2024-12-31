package com.wjy.service;

import com.wjy.dto.ShoppingCartDTO;
import com.wjy.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    List<ShoppingCart> list(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void clean();

    /**
     * 购物车减少
     *
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
