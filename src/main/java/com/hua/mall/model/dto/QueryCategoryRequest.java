package com.hua.mall.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hua.mall.common.PageRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 描述：
 *
 * @author hua
 * @date 2022/10/15 20:54
 */
public class QueryCategoryRequest extends PageRequest implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 分类目录名称
     */
    @NotNull(message = "分类名为空")
    private String name;
    /**
     * 分类目录级别，例如1代表一级，2代表二级，3代表三级
     */
    @Max(3)
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
