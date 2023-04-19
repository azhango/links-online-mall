package com.hua.mall.controller;

import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.entity.User;
import com.hua.mall.model.vo.CartVO;
import com.hua.mall.service.CartService;
import com.hua.mall.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 描述：购物车服务
 *
 * @author hua
 * @date 2022/10/25 14:30
 */
@RestController
@RequestMapping("/cart")
@Api(tags = "购物车服务")
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    @Cacheable("CategoryList")
    public BaseResponse<List<CartVO>> list(HttpServletRequest request) {
        // 内部获取，防止横向越权
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        List<CartVO> cartList = cartService.cartList(currentUser.getId());
        return ResultUtils.success(cartList);
    }

    @PostMapping("/add")
    @ApiOperation("添加")
    public BaseResponse<List<CartVO>> add(@RequestParam Long productId, @RequestParam Integer count, HttpServletRequest request) {
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        List<CartVO> cartVOS = cartService.addProduct(currentUser.getId(), productId, count);
        return ResultUtils.success(cartVOS);
    }

    @PostMapping("/update")
    @ApiOperation("更新")
    public BaseResponse<List<CartVO>> update(@RequestParam Long productId, @RequestParam Integer count, HttpServletRequest request) {
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        List<CartVO> cartVOS = cartService.updateProduct(currentUser.getId(), productId, count);
        return ResultUtils.success(cartVOS);
    }

    @PostMapping("/delete")
    @ApiOperation("删除")
    public BaseResponse<List<CartVO>> delete(@RequestParam Long productId, HttpServletRequest request) {
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        List<CartVO> cartVOS = cartService.deleteProduct(currentUser.getId(), productId);
        return ResultUtils.success(cartVOS);
    }

    @PostMapping("/select")
    @ApiOperation("商品选择状态")
    public BaseResponse<List<CartVO>> selectState(@RequestParam Long productId, @RequestParam Integer selected, HttpServletRequest request) {
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        List<CartVO> cartVOS = cartService.selectState(currentUser.getId(), productId, selected);
        return ResultUtils.success(cartVOS);
    }

    @PostMapping("/select_all")
    @ApiOperation("全部选择状态")
    public BaseResponse<List<CartVO>> selectAllState(@RequestParam Integer selected, HttpServletRequest request) {
        String token = request.getHeader("token");
        User currentUser = JwtUtil.getJwtToken(token);
        List<CartVO> cartVOS = cartService.selectAllState(currentUser.getId(), selected);
        return ResultUtils.success(cartVOS);
    }
}
