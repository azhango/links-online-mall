package com.hua.mall.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品表
 *
 * @TableName product
 */
@TableName(value = "product")
@Data
public class Product implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 商品主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品图片,相对路径地址
     */
    private String image;

    /**
     * 商品详情
     */
    private String detail;
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 价格,单位-分
     */
    private Integer price;

    /**
     * 库存数量
     */
    private Integer stock;
    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 商品上架状态：0-下架，1-上架
     */
    private Integer productStatus;
}