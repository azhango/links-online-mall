package com.hua.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hua.mall.model.dto.OrderStatisticsQuery;
import com.hua.mall.model.entity.Order;
import com.hua.mall.model.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hua
 * @description 针对表【order(订单表;)】的数据库操作Mapper
 * @createDate 2022-10-31 19:20:57
 * @Entity com.hua.mall.model.entity.Order
 */
public interface OrderMapper extends BaseMapper<Order> {

    List<OrderStatisticsVO> selectOrderStatistics(@Param("query") OrderStatisticsQuery query);
}
