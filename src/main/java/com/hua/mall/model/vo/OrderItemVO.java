package com.hua.mall.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单的商品表
 *
 * @author hua
 */
@Data
public class OrderItemVO implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 归属订单id
     */
    private String orderNo;

    /**
     * 商品id
     */
    private Long productId;

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
}