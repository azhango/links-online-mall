package com.hua.mall.constant;

/**
 * 用户常量
 *
 * @author hua
 */
public interface UserConstant {

    /**
     * 登录状态
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 盐值，混淆密码
     */
    String SALT = "{mumu}[mall]+plus";

    /**
     * 账号校验
     */
    String USER_ACCOUNT_CHECK = "^[a-zA-Z0-9_-]{5,16}$";
}
