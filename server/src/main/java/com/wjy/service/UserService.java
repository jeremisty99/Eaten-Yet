package com.wjy.service;

import com.wjy.dto.UserLoginDTO;
import com.wjy.entity.User;
import com.wjy.result.Result;
import com.wjy.vo.UserLoginVO;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(@RequestBody UserLoginDTO userLoginDTO);
}
