package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 添加商品分类请求
 *
 * @author hua
 */
@Data
public class UpdateProductRequest implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 商品名称
     */
    @Size(min = 2, message = "名称最短为2个字符")
    @NotNull(message = "商品名称为空")
    private String productName;
    /**
     * 产品图片,相对路径地址
     */
    private String image;
    /**
     * 商品详情
     */
    private String detail;
    /**
     * 分类id
     */
    @NotNull(message = "分类名为空")
    private Long categoryId;
    /**
     * 价格,单位-分
     */
    @Min(value = 1, message = "价格不等低于1分")
    @NotNull(message = "价格为空")
    private Integer price;
    /**
     * 库存数量
     */
    @Max(value = 10000, message = "库存不能大于10000")
    @NotNull(message = "库存为空")
    private Integer stock;
    /**
     * 商品上架状态：0-下架，1-上架
     */
    @NotNull(message = "商品上架状态为空")
    private Integer productStatus;
}
