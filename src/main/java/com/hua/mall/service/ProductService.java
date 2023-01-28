package com.hua.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hua.mall.common.DeleteRequest;
import com.hua.mall.common.PageRequest;
import com.hua.mall.model.dto.AddProductRequest;
import com.hua.mall.model.dto.ProductQueryRequest;
import com.hua.mall.model.dto.UpdateProductRequest;
import com.hua.mall.model.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author hua
 * @description 针对表【product(商品表)】的数据库操作Service
 * @createDate 2022-10-06 03:04:40
 */
public interface ProductService extends IService<Product> {

    /**
     * 添加商品
     *
     * @param addProductRequest 商品参数
     * @return 商品ID
     */
    long addProduct(AddProductRequest addProductRequest);

    /**
     * 更新商品
     *
     * @param updateProductRequest 更新商品请求
     * @return 更新成功
     */
    void updateProduct(UpdateProductRequest updateProductRequest);

    /**
     * 删除商品
     *
     * @param deleteRequest 删除商品ID
     * @return 删除请求
     */
    void deleteProduct(DeleteRequest deleteRequest);

    /**
     * 批量上下架
     *
     * @param ids        上下架商品ID
     * @param sellStatus 状态
     */
    void BatchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    /**
     * 分页查询
     *
     * @param pageRequest 查询请求
     * @return 分页商品信息
     */
    Page<Product> listProductForAdmin(PageRequest pageRequest);

    /**
     * 查询商品详情
     *
     * @param id 商品id
     * @return 商品信息
     */
    Product detail(Long id);

    /**
     * 分页模糊查询商品(前台)
     *
     * @param productListRequest
     * @return
     */
    Page<Product> searchProduct(ProductQueryRequest productListRequest);

    /**
     * 上传商品图片
     *
     * @param file 图片
     * @return URL
     */
    String uploadImg(MultipartFile file) throws IOException;

    /**
     * 批量上传商品
     *
     * @param file excel
     * @throws IOException excel
     */
    void batchUpdateProduct(MultipartFile file) throws IOException;

    /**
     * 图片水印处理
     *
     * @param file 图片
     * @return 处理后的图片
     */
    String imgWatermark(MultipartFile file) throws IOException;

    /**
     * 批量处理商品更新
     *
     * @param updateProductRequest 多条商品
     */
    void beachUpdateProductList(List<UpdateProductRequest> updateProductRequest);
}
