package com.hua.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.dto.ProductQueryRequest;
import com.hua.mall.model.entity.Product;
import com.hua.mall.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 描述：商品前台服务
 *
 * @author hua
 * @date 2022/10/21 22:10
 */
@RestController
@RequestMapping("/product")
@Validated
@Api(tags = "商品服务")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping("/detail/{id}")
    @ApiOperation("商品详情")
    public BaseResponse<Product> detail(@PathVariable Long id) {
        Product productDetail = productService.detail(id);
        return ResultUtils.success(productDetail);
    }

    /**
     * 模糊查询和排序
     *
     * @param productListRequest
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("批量查询和模糊查询")
    public BaseResponse<Page<Product>> list(@Valid @RequestBody ProductQueryRequest productListRequest) {
        Page<Product> productPage = productService.searchProduct(productListRequest);
        return ResultUtils.success(productPage);
    }
}
