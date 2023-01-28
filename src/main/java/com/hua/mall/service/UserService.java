package com.hua.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hua.mall.model.dto.UserRegisterRequest;
import com.hua.mall.model.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author hua
 * @description 针对表【user(用户表 )】的数据库操作Service
 * @createDate 2022-10-06 02:14:15
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求
     * @return 返回注册成功
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @return 返回登录用户的信息
     */
    String userLogin(String userAccount, String userPassword, HttpSession session);

    /**
     * 获取当前登录用户信息
     *
     * @param request 用户登录态
     * @return 返回当前用户登录的信息
     */
    User loginStatus(HttpServletRequest request);

    /**
     * 退出登录
     *
     * @param request 登录状态
     * @return 移除登录状态
     */
    void logout(HttpServletRequest request);

}
