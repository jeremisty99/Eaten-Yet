package com.wjy.service;

import com.wjy.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 新增地址
     *
     * @param addressBook
     */
    void add(AddressBook addressBook);

    /**
     * 查询全部地址
     *
     * @return
     */
    List<AddressBook> list();

    /**
     * 查询默认地址
     */
    AddressBook getDefaultAddress();

    /**
     * 设置默认地址
     */
    void setDefaultAddress(AddressBook addressBook);

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */
    void updateById(AddressBook addressBook);

    /**
     * 根据id删除地址
     *
     * @param id
     */
    void deleteById(Long id);
}
