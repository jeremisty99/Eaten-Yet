package com.wjy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjy.constant.MessageConstant;
import com.wjy.dto.UserLoginDTO;
import com.wjy.entity.User;
import com.wjy.exception.LoginFailedException;
import com.wjy.mapper.UserMapper;
import com.wjy.properties.WeChatProperties;
import com.wjy.result.Result;
import com.wjy.service.UserService;
import com.wjy.utils.HttpClientUtil;
import com.wjy.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     *
     * @param userLoginDTO
     * @return
     */

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务,获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        String openid = getOpenid(userLoginDTO, map);

        //判断openid,是否为空,加果为空表示登录失败,抛出业务异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        User user = userMapper.selectOne(queryWrapper);
        //如果是新用户,自动完成注册
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /**
     * 调用微信接口服务,获得当前微信用户的openid
     *
     * @param userLoginDTO
     * @param map
     * @return
     */
    private String getOpenid(UserLoginDTO userLoginDTO, Map<String, String> map) {
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
