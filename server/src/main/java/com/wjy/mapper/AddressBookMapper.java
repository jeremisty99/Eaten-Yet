package com.wjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
