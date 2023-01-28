package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hua
 */
@Data
public class UserUpdateRequest implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码，MD5加密
     */
    private String userPassword;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 个性签名
     */
    private String signature;
    /**
     * 邮件地址
     */
    private String emailAddress;
    /**
     * 角色，user，admin
     */
    private String userRole;
}
