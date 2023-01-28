package com.hua.mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单的商品表
 *
 * @author hua
 * @TableName order_item
 */
@TableName(value = "order_item")
@Data
public class OrderItem implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 归属订单id
     */
    private String orderNo;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImg;

    /**
     * 单价（下单时的快照）
     */
    private Integer unitPrice;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品总价
     */
    private Integer totalPrice;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}