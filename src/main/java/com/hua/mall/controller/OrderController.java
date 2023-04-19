package com.hua.mall.controller;

import com.github.pagehelper.PageInfo;
import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.dto.OrderCreateRequest;
import com.hua.mall.model.dto.OrderQueryRequest;
import com.hua.mall.model.vo.OrderVO;
import com.hua.mall.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 描述：订单服务
 *
 * @author hua
 * @date 2022/10/31 13:53
 */
@RestController
@RequestMapping("/order")
@Api(tags = "订单服务")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/create")
    @ApiOperation(value = "创建订单")
    public BaseResponse<String> create(@RequestBody @Valid OrderCreateRequest orderCreateRequest) {
        String orderNo = orderService.createOrder(orderCreateRequest, request);
        return ResultUtils.success(orderNo);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "订单详情")
    public BaseResponse<OrderVO> detail(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ResultUtils.success(orderVO);
    }

    @GetMapping("/list")
    @ApiOperation(value = "订单列表")
    public BaseResponse<PageInfo> list(@RequestBody OrderQueryRequest orderQueryRequest) {
        PageInfo pageInfo = orderService.selectList(orderQueryRequest, request);
        return ResultUtils.success(pageInfo);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "取消订单")
    public BaseResponse cancel(@RequestParam String orderNo) {
        orderService.cancelOrder(orderNo, request);
        return ResultUtils.success(null);
    }

    @GetMapping("/qrcode")
    @ApiOperation(value = "订单支付二维码")
    public BaseResponse<String> qrcode(@RequestParam String orderNo) {
        String qrCode = orderService.getQrCode(orderNo);
        return ResultUtils.success(qrCode);
    }

    @PostMapping("/pay")
    @ApiOperation(value = "支付订单")
    public BaseResponse orderPay(@RequestParam String orderNo) {
        orderService.orderPay(orderNo);
        return ResultUtils.success(null);
    }
}
