package com.wjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjy.constant.MessageConstant;
import com.wjy.constant.PasswordConstant;
import com.wjy.constant.StatusConstant;
import com.wjy.context.BaseContext;
import com.wjy.dto.EmployeeDTO;
import com.wjy.dto.EmployeeLoginDTO;
import com.wjy.dto.EmployeePageQueryDTO;
import com.wjy.dto.PasswordEditDTO;
import com.wjy.entity.Employee;
import com.wjy.exception.AccountLockedException;
import com.wjy.exception.AccountNotFoundException;
import com.wjy.exception.PasswordErrorException;
import com.wjy.mapper.EmployeeMapper;
import com.wjy.result.PageResult;
import com.wjy.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.selectOne(new QueryWrapper<Employee>()
                .eq("username", username));
        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    public void add(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                .status(StatusConstant.ENABLE)
                .build();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.insert(employee);
        BaseContext.removeCurrentId();
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        IPage<Employee> page = new Page<>(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        String name = employeePageQueryDTO.getName() == null ? "" : employeePageQueryDTO.getName();
        queryWrapper.like("name", name);
        employeeMapper.selectPage(page, queryWrapper);
        long total = page.getTotal();
        List<Employee> employees = page.getRecords();
        log.debug("当前页数据：{}", employees);
        return new PageResult(total, employees);
    }

    /**
     * 修改员工状态
     *
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        UpdateWrapper<Employee> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("status", status);
        employeeMapper.update(null, updateWrapper);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Employee employee = employeeMapper.selectOne(queryWrapper);
        employee.setPassword("******"); // 避免将密码返回给客户端
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.updateById(employee);
    }

    /**
     * 修改密码
     *
     * @param passwordEditDTO
     */
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        String oldPwd = passwordEditDTO.getOldPassword();
        // 将前端传过来的旧密码进行MD5加密
        oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
        // 根据id查询当前账号信息
        Employee employee = employeeMapper.selectOne(
                new QueryWrapper<Employee>().eq("id", BaseContext.getCurrentId())
        );
        // 和之前存进数据库的加密的密码进行比对，看看是否一样，不一样要抛异常
        if (!oldPwd.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        String newPwd = passwordEditDTO.getNewPassword();
        newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());
        employee.setPassword(newPwd);
        employeeMapper.updateById(employee);
    }

}
