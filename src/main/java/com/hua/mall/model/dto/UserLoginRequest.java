package com.hua.mall.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author hua
 */
@Data
public class UserLoginRequest implements Serializable {

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
}
