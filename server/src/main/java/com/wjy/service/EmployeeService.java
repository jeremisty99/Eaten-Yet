package com.wjy.service;

import com.wjy.dto.EmployeeDTO;
import com.wjy.dto.EmployeeLoginDTO;
import com.wjy.dto.EmployeePageQueryDTO;
import com.wjy.dto.PasswordEditDTO;
import com.wjy.entity.Employee;
import com.wjy.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    void add(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     *
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     *
     * @param passwordEditDTO
     */
    void editPassword(PasswordEditDTO passwordEditDTO);
}
