package com.wjy.controller.admin;

import com.wjy.dto.CategoryPageQueryDTO;
import com.wjy.dto.CategoryDTO;
import com.wjy.dto.EmployeeDTO;
import com.wjy.dto.EmployeePageQueryDTO;
import com.wjy.entity.Category;
import com.wjy.entity.Employee;
import com.wjy.properties.JwtProperties;
import com.wjy.result.PageResult;
import com.wjy.result.Result;
import com.wjy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Tag(name = "分类管理")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Operation(summary = "分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(@ParameterObject CategoryPageQueryDTO categoryPageQueryDTO) {
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    @Operation(summary = "新增分类")
    @PostMapping
    public Result<String> add(@RequestBody CategoryDTO categoryDTO) {
        categoryService.add(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类状态
     *
     * @param status
     * @param id
     * @return
     */
    @Operation(summary = "修改分类状态")
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, Long id) {
        categoryService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Operation(summary = "根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> getByType(@RequestParam Integer type) {
        List<Category> categories = categoryService.getByType(type);
        return Result.success(categories);
    }

    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @Operation(summary = "根据id删除分类")
    @DeleteMapping()
    public Result deleteById(@RequestParam Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 编辑分类信息
     *
     * @param categoryDTO
     * @return
     */
    @Operation(summary = "编辑分类信息")
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }
}
