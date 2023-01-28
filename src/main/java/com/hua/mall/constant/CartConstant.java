package com.hua.mall.constant;

import org.springframework.stereotype.Component;

/**
 * 描述：购物车状态
 *
 * @author hua
 * @date 2022/10/25 15:18
 */
@Component
public interface CartConstant {

    /**
     * 不出售
     */
    Integer NOT_SALE = 0;
    /**
     * 出售
     */
    Integer SALE = 1;
    /**
     * 不选中
     */
    Integer UN_CHECKED = 0;
    /**
     * 选中
     */
    Integer CHECKED = 1;
}
