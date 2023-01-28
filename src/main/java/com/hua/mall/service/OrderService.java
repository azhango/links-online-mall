package com.hua.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hua.mall.common.PageRequest;
import com.hua.mall.model.dto.OrderCreateRequest;
import com.hua.mall.model.entity.Order;
import com.hua.mall.model.vo.OrderStatisticsVO;
import com.hua.mall.model.vo.OrderVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author hua
 * @description 针对表【order(订单表;)】的数据库操作Service
 * @createDate 2022-10-06 03:10:27
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     *
     * @param orderCreateRequest 订单请求
     * @param request            获取JWT
     * @return 订单信息
     */
    String createOrder(OrderCreateRequest orderCreateRequest, HttpServletRequest request);

    /**
     * 订单详情
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderVO detail(String orderNo);

    /**
     * 查询所有订单
     *
     * @param pageRequest 分页信息
     * @param request
     * @return 所有订单信息
     */
    PageInfo selectList(PageRequest pageRequest, HttpServletRequest request);

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     */
    void cancelOrder(String orderNo, HttpServletRequest request);

    /**
     * 生成支付二维码
     *
     * @param orderNo 订单号
     * @return
     */
    String getQrCode(String orderNo);

    /**
     * 支付订单
     *
     * @param orderNo 订单编号
     */
    void orderPay(String orderNo);

    /**
     * 管理员查询订单列表
     *
     * @param pageRequest 分页请求
     * @return 订单列表
     */
    PageInfo listForAdmin(PageRequest pageRequest);

    /**
     * 管理员已发货
     *
     * @param orderNo 订单编号
     */
    void delivered(String orderNo);

    /**
     * 确认收货
     *
     * @param orderNo 订单号
     */
    void finish(String orderNo);

    /**
     * 订单信息统计
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    List<OrderStatisticsVO> startDate(Date startDate, Date endDate);
}
