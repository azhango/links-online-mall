package com.hua.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hua.mall.annotation.AuthCheck;
import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.DeleteRequest;
import com.hua.mall.common.PageRequest;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.dto.AddProductRequest;
import com.hua.mall.model.dto.UpdateProductRequest;
import com.hua.mall.model.entity.Product;
import com.hua.mall.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 描述： 管理员后台商品
 *
 * @author hua
 * @date 2022/10/18 16:42
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员商品服务")
public class ProductAdminController {

    @Resource
    private ProductService productService;

    @AuthCheck(mustRole = "admin")
    @PostMapping("/product/add")
    @ApiOperation("添加商品")
    public BaseResponse add(@RequestBody @Valid AddProductRequest addProductRequest) {
        long result = productService.addProduct(addProductRequest);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/product/update")
    @ApiOperation("更新商品")
    public BaseResponse update(@RequestBody @Valid UpdateProductRequest updateProductRequest) {
        productService.updateProduct(updateProductRequest);
        return ResultUtils.success(null);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/product/delete")
    @ApiOperation("删除商品")
    public BaseResponse delete(@RequestBody DeleteRequest deleteRequest) {
        productService.deleteProduct(deleteRequest);
        return ResultUtils.success(null);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/product/batchUpdate")
    @ApiOperation("批量更新选择状态")
    public BaseResponse BachUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus) {
        productService.BatchUpdateSellStatus(ids, sellStatus);
        return ResultUtils.success(null);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/product/list")
    @ApiOperation("管理产品列表")
    public BaseResponse<Page<Product>> listProductForAdmin(@RequestBody PageRequest pageRequest) {
        Page<Product> productPage = productService.listProductForAdmin(pageRequest);
        return ResultUtils.success(productPage);
    }

    @PostMapping("/beach_update")
    @ApiOperation("批量更新商品")
    public BaseResponse beachUpdate(@Valid @RequestBody List<UpdateProductRequest> updateProductRequests) {
        productService.beachUpdateProductList(updateProductRequests);
        return ResultUtils.success(null);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/upload/image")
    @ApiOperation("上传图片")
    public BaseResponse uploadImage(@RequestParam("file") MultipartFile file) {
        String result = null;
        try {
            result = productService.uploadImg(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/upload/excel")
    @ApiOperation("上传Excel")
    public BaseResponse uploadProduct(@RequestParam("file") MultipartFile file) throws IOException {
        productService.batchUpdateProduct(file);
        return ResultUtils.success(null);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/upload/watermark")
    @ApiOperation("图片添加水印")
    public BaseResponse imageWatermark(@RequestParam("file") MultipartFile file) throws IOException {
        String result = productService.imgWatermark(file);
        return ResultUtils.success(result);
    }
}
