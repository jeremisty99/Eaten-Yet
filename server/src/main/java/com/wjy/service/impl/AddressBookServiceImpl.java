package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjy.context.BaseContext;
import com.wjy.entity.AddressBook;
import com.wjy.mapper.AddressBookMapper;
import com.wjy.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     *
     * @param addressBook
     */
    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询全部地址
     *
     * @return
     */
    @Override
    public List<AddressBook> list() {
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", BaseContext.getCurrentId());
        return addressBookMapper.selectList(queryWrapper);
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @Override
    public AddressBook getDefaultAddress() {
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", BaseContext.getCurrentId());
        queryWrapper.eq("is_default", 1);
        return addressBookMapper.selectOne(queryWrapper);
    }

    /**
     * 设置默认地址
     */

    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        // 将所有地址is_default设置为0
        addressBookMapper.update(
                AddressBook.builder().isDefault(0).build(),
                new QueryWrapper<AddressBook>().eq("user_id", BaseContext.getCurrentId())
        );
        // 将当前id地址is_default设置为1
        addressBookMapper.update(
                AddressBook.builder().isDefault(1).build(),
                new QueryWrapper<AddressBook>().eq("id", addressBook.getId())
        );
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.selectOne(new QueryWrapper<AddressBook>().eq("id", id));
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    @Override
    public void updateById(AddressBook addressBook) {
        addressBookMapper.updateById(addressBook);
    }


    /**
     * 根据id删除地址
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.delete(new QueryWrapper<AddressBook>().eq("id", id));
    }
}
