package com.hua.mall.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author hua
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;
    /**
     * 账号
     */
    @NotNull(message = "账号为空")
    private String account;
    /**
     * 密码
     */
    @NotNull(message = "密码为空")
    private String password;
    /**
     * 邮箱
     */
    @NotNull(message = "邮箱为空")
    private String emailAddress;
    /**
     * 验证码
     */
    @NotNull(message = "验证码为空")
    private String verificationCode;
}
