package com.wjy.controller.admin;

import com.wjy.dto.SetmealDTO;
import com.wjy.dto.SetmealPageQueryDTO;
import com.wjy.result.PageResult;
import com.wjy.result.Result;
import com.wjy.service.SetmealService;
import com.wjy.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Tag(name = "套餐管理")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @CacheEvict(value = "setmealCache", key = "#{setmealDTO.categoryId}")
    @Operation(summary = "新增套餐")
    @PostMapping()
    public Result add(@RequestBody SetmealDTO setmealDTO) {
        setmealService.add(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Operation(summary = "套餐分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(@ParameterObject SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 套餐批量删除
     *
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @Operation(summary = "套餐批量删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        setmealService.delete(ids);
        return Result.success();
    }

    /**
     * 修改套餐状态
     *
     * @param status
     * @param id
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @Operation(summary = "修改套餐状态")
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, Long id) {
        setmealService.updateStatus(status, id);
        return Result.success();
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


    /**
     * 编辑套餐信息
     *
     * @param setmealDTO
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @Operation(summary = "编辑套餐信息")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }
}
