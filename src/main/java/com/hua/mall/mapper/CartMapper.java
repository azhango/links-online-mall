package com.hua.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hua.mall.model.entity.Cart;
import com.hua.mall.model.vo.CartVO;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author hua
 * @description 针对表【cart(购物车)】的数据库操作Mapper
 * @createDate 2022-10-06 03:15:05
 * @Entity com.hua.mall.model.entity.Cart
 */
public interface CartMapper extends BaseMapper<Cart> {

    List<CartVO> selectCartList(@Param("userId") Long userId);
}




