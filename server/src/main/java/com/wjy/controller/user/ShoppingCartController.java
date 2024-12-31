package com.wjy.controller.user;

import com.wjy.context.BaseContext;
import com.wjy.dto.ShoppingCartDTO;
import com.wjy.entity.ShoppingCart;
import com.wjy.result.Result;
import com.wjy.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Tag(name = "用户购物车接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @Operation(summary = "添加购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @Operation(summary = "查询购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        return Result.success(shoppingCartService.list(shoppingCartDTO));
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clean")
    public Result clean() {
        shoppingCartService.clean();
        return Result.success();
    }

    @Operation(summary = "购物车减少菜品")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.sub(shoppingCartDTO);
        return Result.success();
    }
}
