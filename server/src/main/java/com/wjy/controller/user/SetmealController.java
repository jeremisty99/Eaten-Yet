package com.wjy.controller.user;

import com.wjy.dto.SetmealDTO;
import com.wjy.dto.SetmealPageQueryDTO;
import com.wjy.entity.Dish;
import com.wjy.entity.Setmeal;
import com.wjy.result.PageResult;
import com.wjy.result.Result;
import com.wjy.service.SetmealService;
import com.wjy.vo.DishItemVO;
import com.wjy.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Tag(name = "用户套餐接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     *
     * @param categoryId
     * @return
     */
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    @Operation(summary = "根据分类id查询套餐")
    @GetMapping("/list")
    public Result<List<Setmeal>> getList(@RequestParam(required = false) Long categoryId) {
        List<Setmeal> setmeals = setmealService.getListWithFlavor(categoryId);
        return Result.success(setmeals);
    }

    /**
     * 根据套餐id查询菜品
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据套餐id查询菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> getDishItemById(@PathVariable Long id) {
        List<DishItemVO> dishItemVOS = setmealService.getDishItemById(id);
        return Result.success(dishItemVOS);
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmeales = setmealService.getById(id);
        return Result.success(setmeales);
    }


}
