package com.hua.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hua.mall.common.DeleteRequest;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.common.PageRequest;
import com.hua.mall.constant.CommonConstant;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.mapper.CategoryMapper;
import com.hua.mall.model.dto.AddCategoryRequest;
import com.hua.mall.model.dto.UpdateCategoryRequest;
import com.hua.mall.model.entity.Category;
import com.hua.mall.model.vo.CategoryVO;
import com.hua.mall.service.CategoryService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hua
 * @description 针对表【category(商品分类 )】的数据库操作Service实现
 * @createDate 2022-10-06 03:13:06
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 添加分类
     *
     * @param addCategoryRequest 添加分类请求
     * @return 返回分类主键ID
     */
    @Override
    public long addCategory(AddCategoryRequest addCategoryRequest) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryRequest, category);
        // 1. 判断请求参数是否为空
        if (category == null || category.getName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.查找分类是否存在
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", category.getName());
        Long categoryOld = categoryMapper.selectCount(wrapper);
        if (categoryOld > 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该分类已存在");
        }
        // 3. 插入数据库
        boolean save = this.save(category);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库插入失败");
        }
        return category.getId();
    }

    /**
     * 更新分类请求
     *
     * @param updateCategoryRequest 更新分类请求
     * @return 更新成功
     */
    @Override
    public Boolean updateCategory(UpdateCategoryRequest updateCategoryRequest) {
        // 1. 判断请求参数是否为空
        if (updateCategoryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 查询目录是否存在
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryRequest, category);
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", category.getName());
        Category categoryOld = this.getOne(wrapper);
        if (!categoryOld.getId().equals(category.getId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不能重名");
        }
        // 3. 更新目录
        boolean result = this.updateById(category);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库插入失败");
        }
        return result;
    }

    /**
     * 删除目录
     *
     * @param deleteRequest 目录ID
     * @return 成功删除
     */
    @Override
    public Boolean deleteCategory(DeleteRequest deleteRequest) {
        // 1. 判断参数是否为空
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 查询是否存在
        Long id = deleteRequest.getId();
        Category categoryOld = this.getById(id);
        if (categoryOld == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 删除目录
        boolean result = this.removeById(categoryOld.getId());
        return result;
    }

    /**
     * 分页查询
     *
     * @param pageRequest 查询请求
     * @return
     */
    @Override
    public Page<Category> listCategoryForAdmin(PageRequest pageRequest) {
        // 查询分页数据
        int current = pageRequest.getCurrent();
        int pageSize = pageRequest.getPageSize();
        String sortField = pageRequest.getSortField();
        String sortOrder = pageRequest.getSortOrder();
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Category> categoryPage = this.page(new Page<>(current, pageSize), wrapper);
        return categoryPage;
    }

    /**
     * 递归查询
     *
     * @return
     */
    @Override
    public List<CategoryVO> listCategoryForCustomer(Long categoryId) {
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursiveFindCategoryDirectory(categoryVOList, categoryId);
        return categoryVOList;
    }

    /**
     * 递归查找项目
     */
    private void recursiveFindCategoryDirectory(List<CategoryVO> categoryVOList, Long parentId) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        // 递归获取所有的子类别，并组合成一个目录树
        List<Category> categoryList = categoryMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (int i = 0; i < categoryList.size(); i++) {
                Category category = categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                recursiveFindCategoryDirectory(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}




