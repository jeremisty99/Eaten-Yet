package com.wjy.controller.user;

import com.wjy.entity.AddressBook;
import com.wjy.result.Result;
import com.wjy.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Tag(name = "用户地址管理接口")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @return
     */
    @Operation(summary = "新增地址")
    @PostMapping()
    public Result add(@RequestBody AddressBook addressBook) {
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 查询全部地址
     *
     * @return
     */
    @Operation(summary = "查询全部地址")
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        return Result.success(addressBookService.list());
    }

    /**
     * 查询默认地址
     *
     * @return
     */
    @Operation(summary = "查询默认地址")
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddress() {
        AddressBook defaultAddress = addressBookService.getDefaultAddress();
        if (defaultAddress == null)
            return Result.error("没有默认地址");
        else return Result.success();
    }

    /**
     * 设置默认地址
     *
     * @return
     */
    @Operation(summary = "设置默认地址")
    @PutMapping("/default")
    public Result<AddressBook> setDefaultAddress(@RequestBody AddressBook addressBook) {
        addressBookService.setDefaultAddress(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询地址
     *
     * @return
     */
    @Operation(summary = "根据id查询地址")
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressBookService.getById(id));
    }

    /**
     * 根据id修改地址
     *
     * @return
     */
    @Operation(summary = "根据id修改地址")
    @PutMapping()
    public Result updateById(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     *
     * @return
     */
    @Operation(summary = "根据id删除地址")
    @DeleteMapping()
    public Result deleteById(@RequestParam Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }
}
