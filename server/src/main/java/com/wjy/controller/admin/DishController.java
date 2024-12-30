package com.wjy.controller.admin;

import com.wjy.dto.DishDTO;
import com.wjy.dto.DishPageQueryDTO;
import com.wjy.dto.EmployeeDTO;
import com.wjy.entity.Dish;
import com.wjy.entity.Employee;
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
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Tag(name = "菜品管理")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @Operation(summary = "新增菜品")
    @PostMapping()
    public Result add(@RequestBody DishDTO dishDTO) {
        dishService.addWithFlavor(dishDTO);
        cleanCache("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Operation(summary = "菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(@ParameterObject DishPageQueryDTO dishPageQueryDTO) {
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    @Operation(summary = "菜品批量删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        dishService.delete(ids);
        // 直接删除所有缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 修改菜品状态
     *
     * @param status
     * @param id
     * @return
     */
    @Operation(summary = "修改菜品状态")
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, Long id) {
        dishService.updateStatus(status, id);
        cleanCache("dish_*");
        return Result.success();
    }


    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishes = dishService.getById(id);
        return Result.success(dishes);
    }


    /**
     * 编辑菜品信息
     *
     * @param dishDTO
     * @return
     */
    @Operation(summary = "编辑菜品信息")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.update(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据参数动态查询菜品
     *
     * @param categoryId
     * @param name
     * @return
     */
    @Operation(summary = "根据参数动态查询菜品") // 添加套餐时需要
    @GetMapping("/list")
    public Result<List<Dish>> getList(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) String name) {
        List<Dish> dishes = dishService.getList(categoryId, name);
        return Result.success(dishes);
    }

    /**
     * 清理全部redis缓存
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
