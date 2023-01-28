package com.hua.mall.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hua
 */
@Data
public class UserVO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户账号
     */
    private String userAccount;
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
}
