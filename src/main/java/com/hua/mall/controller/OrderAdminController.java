package com.hua.mall.controller;

import com.github.pagehelper.PageInfo;
import com.hua.mall.annotation.AuthCheck;
import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.PageRequest;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.vo.OrderStatisticsVO;
import com.hua.mall.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 描述：管理员订单服务
 *
 * @author hua
 * @date 2022/11/04 05:18
 */
@RestController
@RequestMapping("/admin/order")
@Api(tags = "管理员订单")
public class OrderAdminController {

    @Resource
    private OrderService orderService;

    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    @ApiOperation("管理员订单列表")
    public BaseResponse listForAdmin(PageRequest pageRequest) {
        PageInfo pageInfo = orderService.listForAdmin(pageRequest);
        return ResultUtils.success(pageInfo);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/delivered")
    @ApiOperation("管理员发货")
    public BaseResponse delivered(@RequestParam String orderNo) {
        orderService.delivered(orderNo);
        return ResultUtils.success(null);
    }

    @PostMapping("/finish")
    @ApiOperation("管理员或用户确认发货")
    public BaseResponse finish(@RequestParam String orderNo) {
        orderService.finish(orderNo);
        return ResultUtils.success(null);
    }

    @PostMapping("/statistics")
    @ApiOperation("每日订单量统计")
    public BaseResponse statistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<OrderStatisticsVO> orderStatisticsVO = orderService.startDate(startDate, endDate);
        return ResultUtils.success(orderStatisticsVO);
    }
}
