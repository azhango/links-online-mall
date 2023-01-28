package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hua.mall.common.PageRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品表
 *
 * @author hua
 * @TableName product
 */
@Data
public class ProductQueryRequest extends PageRequest implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 商品名称
     */
    @NotNull(message = "分类名不能为空")
    private String keyword;
}