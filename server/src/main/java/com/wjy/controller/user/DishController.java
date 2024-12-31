package com.wjy.controller.user;

import com.wjy.dto.DishDTO;
import com.wjy.dto.DishPageQueryDTO;
import com.wjy.entity.Dish;
import com.wjy.result.PageResult;
import com.wjy.result.Result;
import com.wjy.service.DishService;
import com.wjy.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Tag(name = "用户菜品接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据参数动态查询菜品
     *
     * @param categoryId
     * @param name
     * @return
     */
    @Operation(summary = "根据参数动态查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> getList(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) String name) {
        List<DishVO> dishes = null;
        // 查询redis中是否有菜品数据
        if (categoryId != null) {
            String key = "dish_" + categoryId;
            dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
            if (dishes != null && !dishes.isEmpty()) {
                // 缓存命中 无需查询数据库
                return Result.success(dishes);
            }
            dishes = dishService.getListWithFlavor(categoryId, name);
            // 将查询结果存入redis
            redisTemplate.opsForValue().set(key, dishes);
        } else {
            dishes = dishService.getListWithFlavor(null, name);
        }

        return Result.success(dishes);
    }

}
