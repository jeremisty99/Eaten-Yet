package com.wjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.annotation.AutoFill;
import com.wjy.entity.Category;
import com.wjy.entity.Employee;
import com.wjy.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
