package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单表;
 *
 * @author hua
 * @TableName order
 */
@Data
public class OrderCreateRequest implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 收货人姓名快照
     */
    @NotNull(message = "收件人为空")
    private String receiverName;
    /**
     * 收货人手机号快照
     */
    @NotNull(message = "收件电话为空")
    @Min(11)
    private String receiverMobile;
    /**
     * 收货地址快照
     */
    @NotNull(message = "收件人地址为空")
    private String receiverAddress;
}