package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 添加商品分类请求
 *
 * @author hua
 */
@Data
public class AddCategoryRequest {
    @TableField(exist = false)
    public static final long serialVersionUID = 1L;

    /**
     * 分类目录名称
     */
    @Size(min = 2, message = "名称最短为2个字符")
    @NotNull(message = "目录名称不能为空")
    private String name;
    /**
     * 分类目录级别，例如1代表一级，2代表二级，3代表三级
     */
    @Max(3)
    @NotNull(message = "分类不能大于3级目录")
    private Integer type;
    /**
     * 父id，也就是上一级目录的id，如果是一级目录，那么父id为0
     */
    @NotNull(message = "父类目录不能为空")
    private Integer parentId;
    /**
     * 目录展示时的排序
     */
    @NotNull(message = "展示时的排序不能为空")
    private Integer orderNum;
}
