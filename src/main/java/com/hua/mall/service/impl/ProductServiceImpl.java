package com.hua.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hua.mall.common.DeleteRequest;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.common.PageRequest;
import com.hua.mall.constant.CommonConstant;
import com.hua.mall.constant.UploadConstant;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.mapper.ProductMapper;
import com.hua.mall.model.dto.AddProductRequest;
import com.hua.mall.model.dto.ProductQueryRequest;
import com.hua.mall.model.dto.UpdateProductRequest;
import com.hua.mall.model.entity.Product;
import com.hua.mall.service.ProductService;
import com.hua.mall.service.UploadService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.hua.mall.constant.CommonConstant.SORT_ORDER_ASC;

/**
 * @author hua
 * @description 针对表【product(商品表)】的数据库操作Service实现
 * @createDate 2022-10-06 03:04:40
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private UploadService uploadService;

    @Value("${web.uri}")
    private String uri;

    /**
     * 添加商品
     *
     * @param addProductRequest 商品参数
     * @return 商品ID
     */
    @Override
    public long addProduct(AddProductRequest addProductRequest) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductRequest, product);
        // 1. 判断参数是否为空
        if (product == null || product.getProductName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 判断是否存在
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("product_name", product.getProductName());
        Long count = productMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该商品已存在");
        }
        // 3. 插入数据库
        boolean result = this.save(product);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库操作失败");
        }
        return product.getId();
    }

    /**
     * 更新商品
     *
     * @param updateProductRequest 更新商品请求
     * @return 更新成功
     */
    @Override
    public void updateProduct(UpdateProductRequest updateProductRequest) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductRequest, product);
        // 判断商品参数是不是空的
        if (product == null || product.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查找要修改的名称是否存在
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.eq("product_name", product.getProductName());
        Long count = productMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品名称已存在");
        }
        // 更新商品
        boolean result = this.updateById(product);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库更新失败");
        }
    }

    /**
     * 删除商品
     *
     * @param deleteRequest 删除商品ID
     * @return true
     */
    @Override
    public void deleteProduct(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 商品是否存在
        Long id = deleteRequest.getId();
        Product productOld = this.getById(id);
        if (productOld == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品不存在");
        }
        // 删除商品
        boolean result = this.removeById(productOld.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库操作失败");
        }
    }

    /**
     * 批量上下架
     *
     * @param ids        上下架商品ID
     * @param sellStatus 状态
     */
    @Override
    public void BatchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        UpdateWrapper<Product> wrapper = new UpdateWrapper<>();
        wrapper.in("id", ids);
        wrapper.set("product_status", sellStatus);
        boolean result = this.update(wrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 分页查询
     *
     * @param pageRequest 查询请求
     * @return 分页商品信息
     */
    @Override
    public Page<Product> listProductForAdmin(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 当前页面
        long current = pageRequest.getCurrent();
        // 页面大小
        long pageSize = pageRequest.getPageSize();
        // 排序字段
        String sortField = pageRequest.getSortField();
        // 排序顺序
        String sortOrder = pageRequest.getSortOrder();
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField);
        Page<Product> productPage = this.page(new Page<>(current, pageSize), wrapper);
        return productPage;
    }

    /**
     * 查询商品详情
     *
     * @param id 商品id
     * @return 商品信息
     */
    @Override
    public Product detail(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Product productDetail = this.getById(id);
        if (productDetail == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库异常");
        }
        return productDetail;
    }

    /**
     * 分页模糊查询商品(前台)
     *
     * @param productQueryRequest
     * @return
     */
    @Cacheable(value = "searchProduct")
    @Override
    public Page<Product> searchProduct(ProductQueryRequest productQueryRequest) {
        if (productQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        // 当前页面
        long current = productQueryRequest.getCurrent();
        // 分页大小
        long pageSize = productQueryRequest.getPageSize();
        // 排序字段
        String sortField = productQueryRequest.getSortField();
        // 排序顺序
        String sortOrder = productQueryRequest.getSortOrder();
        // 商品昵称
        String keyword = productQueryRequest.getKeyword();
        wrapper.like(StringUtils.isNotBlank(keyword), "product_name", keyword);
        wrapper.eq("product_status", 1);
        wrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Product> productPage = this.page(new Page<>(current, pageSize), wrapper);
        return productPage;
    }

    /**
     * 上传图片
     *
     * @param file 图片
     * @return Url
     */
    @Override
    public String uploadImg(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 使用UUID生成新的文件名
        String newFileName = uploadService.newFileName(file);
        File fileDirectory = new File(UploadConstant.FILE_UPLOAD_DIR);
        File destFile = new File(UploadConstant.FILE_UPLOAD_DIR + newFileName);
        // 创建文件
        uploadService.createFile(file, fileDirectory, destFile);
        // 获取URL
        String address = uri;
        String result = "http://" + address + "/images/" + newFileName;
        return result;
    }

    /**
     * 批量上传商品
     *
     * @param file 文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateProduct(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        String newFileName = uploadService.newFileName(file);
        // 创建文件夹
        File fileDirectory = new File(UploadConstant.FILE_UPLOAD_DIR);
        File destFile = new File(UploadConstant.FILE_UPLOAD_DIR + newFileName);
        // 创建文件
        uploadService.createFile(file, fileDirectory, destFile);
        try {
            List<Product> productList = uploadService.readProductsFromExcel(destFile);
            for (int i = 0; i < productList.size(); i++) {
                Product product = productList.get(i);
                Product oleProduct = this.getOne(new QueryWrapper<Product>().eq("product_name", product.getProductName()));
                if (oleProduct != null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品已存在");
                }
                if (!this.save(product)) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "新增失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片水印
     *
     * @param file 图片
     * @return
     * @throws IOException
     */
    @Override
    public String imgWatermark(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        String newFileName = uploadService.newFileName(file);
        File fileDirectory = new File(UploadConstant.FILE_UPLOAD_DIR);
        File destFile = new File(UploadConstant.FILE_UPLOAD_DIR + newFileName);
        // 创建文件
        uploadService.createFile(file, fileDirectory, destFile);
        // 图片加水印
        BufferedImage bufferedImage = ImageIO.read(new File(UploadConstant.FILE_UPLOAD_DIR + UploadConstant.WATER_MARK_JPG));
        Thumbnails.of(destFile)
                .scale(UploadConstant.SCALE_SIZE)
                .watermark(Positions.BOTTOM_RIGHT, bufferedImage, UploadConstant.IMAGE_OPACITY)
                .toFile(new File(UploadConstant.FILE_UPLOAD_DIR + newFileName));
        String address = uri;
        String result = "http://" + address + "/images/" + newFileName;
        return result;
    }

    /**
     * 批量处理商品更新
     *
     * @param updateProductRequest 多条商品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void beachUpdateProductList(List<UpdateProductRequest> updateProductRequest) {
        if (updateProductRequest.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        for (int i = 0; i < updateProductRequest.size(); i++) {
            UpdateProductRequest productRequest = updateProductRequest.get(i);
            Product product = new Product();
            BeanUtils.copyProperties(productRequest, product);
            // 判断商品参数是不是空的
            if (product == null || product.getId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // 查找要修改的名称是否存在
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("product_name", product.getProductName());
            Long count = productMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品名称已存在");
            }
            // 更新商品
            Boolean result = this.updateById(product);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库更新失败");
            }
        }
    }
}




