package com.hua.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hua.mall.annotation.AuthCheck;
import com.hua.mall.common.BaseResponse;
import com.hua.mall.common.DeleteRequest;
import com.hua.mall.common.PageRequest;
import com.hua.mall.common.ResultUtils;
import com.hua.mall.model.dto.AddCategoryRequest;
import com.hua.mall.model.dto.UpdateCategoryRequest;
import com.hua.mall.model.entity.Category;
import com.hua.mall.model.vo.CategoryVO;
import com.hua.mall.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 商品分类服务(后台)
 *
 * @author hua
 */
@RestController
@RequestMapping("/admin/category")
@Api(tags = "管理员分类")
@Slf4j
public class CategoryAdminController {

    @Resource
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @AuthCheck(mustRole = "admin")
    @PostMapping("/add")
    @ApiOperation("添加")
    public BaseResponse<Long> add(@RequestBody @Valid AddCategoryRequest addCategoryRequest) {
        // 3. 返回成功的分类id
        long category = categoryService.addCategory(addCategoryRequest);
        RBucket<List<CategoryVO>> bucket = redissonClient.getBucket("categoryList");
        if (bucket.isExists()) {
            if (bucket.delete()) {
                log.info("更新了 {} Redis缓存", bucket.getName());
            }
        }
        return ResultUtils.success(category);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/update")
    @ApiOperation("更新")
    public BaseResponse<Boolean> update(@RequestBody @Valid UpdateCategoryRequest updateCategoryRequest) {
        // 3. 更新目录
        Boolean result = categoryService.updateCategory(updateCategoryRequest);
        RBucket<List<CategoryVO>> bucket = redissonClient.getBucket("categoryList");
        if (bucket.isExists()) {
            if (bucket.delete()) {
                log.info("更新了 {} Redis缓存", bucket.getName());
            }
        }
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/delete")
    @ApiOperation("删除")
    public BaseResponse<Boolean> delete(@RequestBody @Valid DeleteRequest deleteRequest) {
        // 3. 删除目录
        Boolean result = categoryService.deleteCategory(deleteRequest);
        RBucket<List<CategoryVO>> bucket = redissonClient.getBucket("categoryList");
        if (bucket.isExists()) {
            if (bucket.delete()) {
                log.info("更新了 {} Redis缓存", bucket.getName());
            }
        }
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = "admin")
    @PostMapping("/list")
    @ApiOperation("后台分类列表")
    public BaseResponse<Page<Category>> listCategoryForAdmin(@RequestBody PageRequest pageRequest) {
        // 分页查询数据
        Page<Category> categoryPage = categoryService.listCategoryForAdmin(pageRequest);
        return ResultUtils.success(categoryPage);
    }

}
