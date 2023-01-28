package com.hua.mall.controller;

import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.vo.CategoryVO;
import com.hua.mall.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 描述：商品分类服务(前台)
 *
 * @author hua
 * @date 2022/10/18 15:47
 */
@RestController
@RequestMapping("/category")
@Api(tags = "分类服务")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("分类列表")
    public BaseResponse<List<CategoryVO>> listCategoryForCustomer() {
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0L);
        return ResultUtils.success(categoryVOS);
    }
}
