package com.hua.mall.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
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
    private String personalizedSignature;
    /**
     * 邮件地址
     */
    private String emailAddress;
    /**
     * 角色，user，admin
     */
    private String userRole;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除 默认0不删除 1-删除
     */
    @TableLogic
    private Integer isDelete;
}