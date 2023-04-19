package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hua.mall.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hua
 * @TableName order
 */
@Data
public class OrderQueryRequest extends PageRequest implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单号（非主键id）
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单总价格
     */
    private Integer totalPrice;

    /**
     * 收货人手机号快照
     */
    private String receiverMobile;

    /**
     * 订单状态: 0用户已取消，10未付款（初始状态），20已付款，30已发货，40交易完成
     */
    private Integer orderStatus;


    /**
     * 支付类型,1-在线支付
     */
    private Integer paymentType;

    /**
     * 发货时间
     */
    private Date deliveryTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 交易完成时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;
}