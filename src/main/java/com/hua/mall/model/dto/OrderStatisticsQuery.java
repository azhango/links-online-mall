package com.hua.mall.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/11/15 19:10
 */
@Data
public class OrderStatisticsQuery implements Serializable {

    private Date startDate;

    private Date endDate;
}
