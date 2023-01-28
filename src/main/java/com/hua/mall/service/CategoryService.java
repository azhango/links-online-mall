package com.hua.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hua.mall.common.DeleteRequest;
import com.hua.mall.common.PageRequest;
import com.hua.mall.model.dto.AddCategoryRequest;
import com.hua.mall.model.dto.UpdateCategoryRequest;
import com.hua.mall.model.entity.Category;
import com.hua.mall.model.vo.CategoryVO;

import java.util.List;

/**
 * @author hua
 * @description 针对表【category(商品分类 )】的数据库操作Service
 * @createDate 2022-10-06 03:13:06
 */
public interface CategoryService extends IService<Category> {
    /**
     * 添加分类
     *
     * @param addCategoryRequest 添加分类请求
     * @return 成功添加
     */
    long addCategory(AddCategoryRequest addCategoryRequest);

    /**
     * 更新分类
     *
     * @param updateCategoryRequest 更新分类请求
     * @return 更信成功
     */
    Boolean updateCategory(UpdateCategoryRequest updateCategoryRequest);

    /**
     * 删除目录
     *
     * @param deleteRequest 目录ID
     * @return 成功删除
     */
    Boolean deleteCategory(DeleteRequest deleteRequest);

    /**
     * 分页查询
     *
     * @param pageRequest 查询请求
     * @return
     */
    Page<Category> listCategoryForAdmin(PageRequest pageRequest);

    /**
     * 递归查询
     *
     * @return
     */
    List<CategoryVO> listCategoryForCustomer(Long categoryId);
}
