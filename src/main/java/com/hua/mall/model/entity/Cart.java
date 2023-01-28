package com.hua.mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车
 *
 * @author hua
 * @TableName cart
 */
@TableName(value = "cart")
@Data
public class Cart implements Serializable {
    /**
     * 购物车id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 是否已勾选：0代表未勾选，1代表已勾选
     */
    private Integer selected;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 用户id
     */
    private Long userId;
}