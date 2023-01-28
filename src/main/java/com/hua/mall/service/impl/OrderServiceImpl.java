package com.hua.mall.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.common.PageRequest;
import com.hua.mall.constant.CartConstant;
import com.hua.mall.constant.UploadConstant;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.mapper.CartMapper;
import com.hua.mall.mapper.OrderItemMapper;
import com.hua.mall.mapper.OrderMapper;
import com.hua.mall.mapper.ProductMapper;
import com.hua.mall.model.dto.OrderCreateRequest;
import com.hua.mall.model.dto.OrderStatisticsQuery;
import com.hua.mall.model.entity.Order;
import com.hua.mall.model.entity.OrderItem;
import com.hua.mall.model.entity.Product;
import com.hua.mall.model.entity.User;
import com.hua.mall.model.enums.OrderCode;
import com.hua.mall.model.vo.CartVO;
import com.hua.mall.model.vo.OrderItemVO;
import com.hua.mall.model.vo.OrderStatisticsVO;
import com.hua.mall.model.vo.OrderVO;
import com.hua.mall.service.CartService;
import com.hua.mall.service.OrderService;
import com.hua.mall.service.UserService;
import com.hua.mall.utils.OrderCodeFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hua.mall.model.enums.OrderCode.CANCELLED;
import static com.hua.mall.model.enums.OrderCode.UNPAID;

/**
 * @author hua
 * @description 针对表【order(订单表;)】的数据库操作Service实现
 * @createDate 2022-10-06 03:10:27
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Resource
    private CartService cartService;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private UserService userService;

    @Value("${web.uri}")
    private String uri;

    /**
     * 创建订单
     *
     * @param orderCreateRequest 订单请求
     * @param request            获取JWT
     * @return 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(OrderCreateRequest orderCreateRequest, HttpServletRequest request) {
        //拿到用户ID
        Long userId = userService.loginStatus(request).getId();
        //从购物车查找已经勾选的商品
        List<CartVO> cartVOList = cartService.cartList(userId);
        ArrayList<CartVO> cartVOListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(1)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        //如果购物车已勾选的为空，报错
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择商品");
        }
        //判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        //把购物车对象转为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            Product product = productMapper.selectById(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "该商品库存不足");
            }
            product.setStock(stock);
            productMapper.updateById(product);
        }
        //把购物车中的已勾选商品删除
        cleanCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(userId);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(orderCreateRequest.getReceiverName());
        order.setReceiverMobile(orderCreateRequest.getReceiverMobile());
        order.setReceiverAddress(orderCreateRequest.getReceiverAddress());
        order.setOrderStatus(UNPAID.getCode());
        //插入到Order表
        orderMapper.insert(order);

        //循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insert(orderItem);
        }
        //把结果返回
        return orderNo;
    }

    /**
     * 计算总价
     */
    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    /**
     * 把购物车中的已勾选商品删除
     */
    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            cartMapper.deleteById(cartVO);
        }
    }

    /**
     * 把购物车对象转为订单item对象
     */
    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        // 创建一个暂存的 List
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    /**
     * 判断商品是否存在、上下架状态、库存
     */
    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectById(cartVO.getProductId());
            //判断商品是否存在，商品是否上架
            if (product == null || product.getProductStatus().equals(CartConstant.NOT_SALE)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在或已下架");
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存不足");
            }
        }
    }

    /**
     * 订单详情
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Override
    public OrderVO detail(String orderNo) {
        // 判断订单号参数是否为空
        if (!StringUtils.hasText(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 按订单号查找订单信息
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        // 判断查找的订单是否为空
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 将查找到的订单信息Copy到OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        // 查找OrderItemVO
        List<OrderItem> orderItemList = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_no", orderNo));
        List<OrderItemVO> orderItemArrayList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemArrayList.add(orderItemVO);
        }
        // 设置订单状态名称
        orderVO.setOrderStatusName(OrderCode.codeOf(orderVO.getOrderStatus()).getMsg());
        // 设置订单下各商品详情
        orderVO.setOrderItemVOList(orderItemArrayList);
        return orderVO;
    }

    /**
     * 查询所有订单
     *
     * @param pageRequest 分页信息
     * @param request
     * @return 所有订单信息
     */
    @Override
    public PageInfo selectList(PageRequest pageRequest, HttpServletRequest request) {
        Long userId = userService.loginStatus(request).getId();
        // 当前页
        long current = pageRequest.getCurrent();
        // 当前页记录数
        long pageSize = pageRequest.getPageSize();
        PageHelper.startPage((int) current, (int) pageSize);
        // 查找所有订单信息
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper
                .eq("user_id", userId)
                .orderByDesc("create_time");
        List<Order> orderList = orderMapper.selectList(orderWrapper);
        // Order 转换为 OrderVO
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /**
     * Order 转换为 OrderVO
     */
    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            // 查找OrderItemVO
            List<OrderItem> orderItemList = orderItemMapper.selectList(
                    new QueryWrapper<OrderItem>().eq("order_no", orderVO.getOrderNo()));
            List<OrderItemVO> orderItemArrayList = new ArrayList<>();
            for (int j = 0; j < orderItemList.size(); j++) {
                OrderItem orderItem = orderItemList.get(j);
                OrderItemVO orderItemVO = new OrderItemVO();
                BeanUtils.copyProperties(orderItem, orderItemVO);
                orderItemArrayList.add(orderItemVO);
            }
            // 设置订单状态名称
            orderVO.setOrderStatusName(OrderCode.codeOf(orderVO.getOrderStatus()).getMsg());
            // 设置订单下各商品详情
            orderVO.setOrderItemVOList(orderItemArrayList);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     */
    @Override
    public void cancelOrder(String orderNo, HttpServletRequest request) {
        if (StrUtil.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前用户
        User currentUser = userService.loginStatus(request);
        UpdateWrapper<Order> wrapper = new UpdateWrapper<>();
        wrapper
                .eq("order_no", orderNo)
                .eq("user_id", currentUser.getId())
                .eq("order_status", UNPAID.getCode())
                .eq("order_status", UNPAID.getCode())
                .set("order_status", CANCELLED.getCode())
                .set("end_time", new Date());
        // 更新失败抛出异常
        boolean update = this.update(wrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该订单不属于你或该订单已付款");
        }
    }

    /**
     * 生成支付二维码
     *
     * @param orderNo 订单号
     * @return
     */
    @Override
    public String getQrCode(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件为空");
        }
        // 生成地址
        String address = uri;
        String payUrl = "http://" + address + "/api/order/pay?orderNo=" + orderNo;
        // 生成二维码
        QrCodeUtil.generate(payUrl, 300, 300,
                FileUtil.file(UploadConstant.FILE_UPLOAD_DIR + "/" + orderNo + ".png"));
        String result = "http://" + address + "/images/" + orderNo + ".png";
        return result;
    }

    /**
     * 支付订单
     *
     * @param orderNo 订单编号
     */
    @Override
    public void orderPay(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 按订单号查找订单信息
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        // 判断查找的订单是否为空
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // order的当前状态是不是未付款
        if (!order.getOrderStatus().equals(UNPAID.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付已取消或订单已支付");
        }
        order.setOrderStatus(OrderCode.PAID.getCode());
        order.setPayTime(new Date());
        boolean update = this.updateById(order);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 管理员查询订单列表
     *
     * @param pageRequest 分页请求
     * @return 订单列表
     */
    @Override
    public PageInfo listForAdmin(PageRequest pageRequest) {
        // 当前页
        long current = pageRequest.getCurrent();
        // 当前页记录数
        long pageSize = pageRequest.getPageSize();
        PageHelper.startPage((int) current, (int) pageSize);
        // 查找所有订单信息
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.orderByDesc("create_time");
        List<Order> orderList = orderMapper.selectList(orderWrapper);
        // Order 转换为 OrderVO
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /**
     * 管理员已发货
     *
     * @param orderNo 订单编号
     */
    @Override
    public void delivered(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 按订单号查找订单信息
        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        // 判断查找的订单是否为空
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // order的当前状态是不是未付款
        if (!order.getOrderStatus().equals(OrderCode.PAID.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付已取消或未完成支付");
        }
        order.setOrderStatus(OrderCode.SHIPPED.getCode());
        order.setDeliveryTime(new Date());
        boolean update = this.updateById(order);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 确认收货
     *
     * @param orderNo 订单号
     */
    @Override
    public void finish(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        User loginUser = userService.loginStatus(request);
        // 按订单号查找订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        Order order = orderMapper.selectOne(wrapper);
        // 如果是普通用户需要校验订单所属
        if (!loginUser.getUserRole().equals("admin") && !order.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "此订单不是你的");
        }
        // 判断查找的订单是否为空
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // order的当前状态是不是未付款
        if (!order.getOrderStatus().equals(OrderCode.SHIPPED.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付已取消或未完成支付");
        }
        order.setOrderStatus(OrderCode.THE_DEAL.getCode());
        order.setDeliveryTime(new Date());
        boolean update = this.updateById(order);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 订单信息统计
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    @Override
    public List<OrderStatisticsVO> startDate(Date startDate, Date endDate) {
        OrderStatisticsQuery orderStatisticsQuery = new OrderStatisticsQuery();
        orderStatisticsQuery.setStartDate(startDate);
        orderStatisticsQuery.setEndDate(endDate);
        List<OrderStatisticsVO> orderStatisticsVOS = orderMapper.selectOrderStatistics(orderStatisticsQuery);
        return orderStatisticsVOS;
    }
}
