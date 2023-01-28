package com.hua.mall.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：订单统计VO
 *
 * @author hua
 * @date 2022/11/15 19:16
 */
@Data
public class OrderStatisticsVO implements Serializable {

    private Date days;

    private Integer amount;
}
