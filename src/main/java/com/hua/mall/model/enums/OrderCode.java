package com.hua.mall.model.enums;

import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;

/**
 * 描述：支付状态枚举
 *
 * @author hua
 * @date 2022/10/31 15:15
 */
public enum OrderCode {
    CANCELLED(0, "订单已取消"),
    UNPAID(10, "未付款"),
    PAID(20, "已付款"),
    SHIPPED(30, "已发货"),
    THE_DEAL(40, "交易完成");

    private int code;
    private String msg;

    OrderCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static OrderCode codeOf(int code) {
        for (OrderCode orderCode : values()) {
            if (orderCode.getCode() == code) {
                return orderCode;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未找到对应状态");
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
