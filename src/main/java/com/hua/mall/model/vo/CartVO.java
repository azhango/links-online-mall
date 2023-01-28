package com.hua.mall.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/10/25 14:55
 */
@Data
public class CartVO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 购物车id
     */
    private Integer id;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 是否已勾选：0代表未勾选，1代表已勾选
     */
    private Integer selected;

    /**
     * 商品单价
     */
    private Integer price;

    /**
     * 总价
     */
    private Integer totalPrice;

    /**
     * 商品名字
     */
    private String productName;

    /**
     * 商品图
     */
    private String productImage;
}
