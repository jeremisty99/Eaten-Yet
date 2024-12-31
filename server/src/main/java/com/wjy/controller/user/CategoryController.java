package com.wjy.controller.user;

import com.wjy.dto.CategoryDTO;
import com.wjy.dto.CategoryPageQueryDTO;
import com.wjy.entity.Category;
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

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Tag(name = "用户分类接口")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 根据类型查询分类
     *
     * @param type
     * @return
     */
    @Operation(summary = "查询分类")
    @GetMapping("/list")
    public Result<List<Category>> getByType(@RequestParam(required = false) Integer type) {
        List<Category> categories = categoryService.getByType(type);
        return Result.success(categories);
    }

}
