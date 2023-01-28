package com.hua.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hua.mall.model.entity.Cart;
import com.hua.mall.model.vo.CartVO;

import java.util.List;

/**
 * @author hua
 * @description 针对表【cart(购物车)】的数据库操作Service
 * @createDate 2022-10-06 03:15:06
 */
public interface CartService extends IService<Cart> {
    /**
     * 商品列表
     *
     * @param userId 用户id
     * @return 当前用户购物车列表
     */
    List<CartVO> cartList(Long userId);

    /**
     * 添加商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param count     商品数量
     * @return 商品
     */
    List<CartVO> addProduct(Long userId, Long productId, Integer count);

    /**
     * 更新商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param count     商品数量
     * @return 商品
     */
    List<CartVO> updateProduct(Long userId, Long productId, Integer count);

    /**
     * 删除商品
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 商品
     */
    List<CartVO> deleteProduct(Long userId, Long productId);

    /**
     * 选择商品状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param selected  状态
     * @return 状态
     */
    List<CartVO> selectState(Long userId, Long productId, Integer selected);

    /**
     * 全选状态
     *
     * @param userId   用户ID
     * @param selected 状态
     * @return
     */
    List<CartVO> selectAllState(Long userId, Integer selected);
}
