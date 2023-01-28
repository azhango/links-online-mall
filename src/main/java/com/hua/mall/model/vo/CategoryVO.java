package com.hua.mall.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类
 *
 * @author hua
 * @TableName category
 */
@Data
public class CategoryVO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分类目录名称
     */
    private String name;
    /**
     * 分类目录级别，例如1代表一级，2代表二级，3代表三级
     */
    private Integer type;
    /**
     * 父id，也就是上一级目录的id，如果是一级目录，那么父id为0
     */
    private Integer parentId;
    /**
     * 目录展示时的排序
     */
    private Integer orderNum;

    private List<CategoryVO> childCategory = new ArrayList<>();
}