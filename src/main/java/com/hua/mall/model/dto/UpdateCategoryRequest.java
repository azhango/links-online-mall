package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 分类更新请求
 *
 * @author hua
 */
@Data
public class UpdateCategoryRequest implements Serializable {

    @TableField(exist = false)
    public static final long serialVersionUID = 1L;

    /**
     * 分类目录名称
     */
    @Size(min = 2, max = 5, message = "目录名称最小2个字，最大5个字")
    @NotNull(message = "分类名称为空")
    private String name;

    /**
     * 分类目录级别，例如1代表一级，2代表二级，3代表三级
     */
    @Max(value = 3, message = "分类不能大于3级目录")
    @NotNull(message = "目录级别为空")
    private Integer type;

    /**
     * 父id，也就是上一级目录的id，如果是一级目录，那么父id为0
     */
    @NotNull(message = "父目录为空")
    private Integer parentId;

    /**
     * 目录展示时的排序
     */
    @NotNull(message = "展示时排序为空")
    private Integer orderNum;
}
