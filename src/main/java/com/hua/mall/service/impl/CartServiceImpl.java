package com.hua.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hua.mall.common.ErrorCode;
import com.hua.mall.exception.BusinessException;
import com.hua.mall.mapper.CartMapper;
import com.hua.mall.mapper.ProductMapper;
import com.hua.mall.model.entity.Cart;
import com.hua.mall.model.entity.Product;
import com.hua.mall.model.vo.CartVO;
import com.hua.mall.service.CartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hua.mall.constant.CartConstant.CHECKED;
import static com.hua.mall.constant.CartConstant.NOT_SALE;

/**
 * @author hua
 * @description 针对表【cart(购物车)】的数据库操作Service实现
 * @createDate 2022-10-06 03:15:06
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>
        implements CartService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CartMapper cartMapper;

    @Override
    public List<CartVO> cartList(Long userId) {
        List<CartVO> cartVOS = cartMapper.selectCartList(userId);
        for (int i = 0; i < cartVOS.size(); i++) {
            CartVO cartVO = cartVOS.get(i);
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }

    @Override
    public List<CartVO> addProduct(Long userId, Long productId, Integer count) {
        // 判断商品是否为空或售卖状态
        Product product = productMapper.selectById(productId);
        if (product == null || product.getProductStatus().equals(NOT_SALE)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断库存
        if (count > product.getStock()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存不足");
        }
        // 查找购物车内商品是否存在
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId).eq("user_id", userId);
        Cart cart = this.getOne(wrapper);
        if (cart == null) {
            // 这个商品不存在购物车
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(count);
            cart.setSelected(CHECKED);
            this.save(cart);
        } else {
            // 商品已存在，将数量叠加
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(CHECKED);
            this.updateById(cartNew);
        }
        return this.cartList(userId);
    }

    @Override
    public List<CartVO> updateProduct(Long userId, Long productId, Integer count) {
        // 判断商品是否为空或售卖状态
        Product product = productMapper.selectById(productId);
        if (product == null || product.getProductStatus().equals(NOT_SALE)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断库存
        if (count > product.getStock()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "库存不足");
        }
        // 查找购物车内商品是否存在
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId).eq("user_id", userId);
        Cart cart = this.getOne(wrapper);
        if (cart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        } else {
            // 更新商品
            cart.setQuantity(count);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setSelected(CHECKED);
            this.updateById(cart);
        }
        return this.cartList(userId);
    }

    @Override
    public List<CartVO> deleteProduct(Long userId, Long productId) {
        // 查找购物车内商品是否存在
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId).eq("user_id", userId);
        Cart cart = this.getOne(wrapper);
        if (cart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        this.removeById(cart);
        return this.cartList(userId);
    }

    @Override
    public List<CartVO> selectState(Long userId, Long productId, Integer selected) {
        // 查找购物车内商品是否存在
        UpdateWrapper<Cart> wrapper = new UpdateWrapper<>();
        wrapper
                .in("product_id", productId)
                .eq("user_id", userId)
                .set("selected", selected);
        boolean update = this.update(wrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return this.cartList(userId);
    }

    @Override
    public List<CartVO> selectAllState(Long userId, Integer selected) {
        // 查找购物车内商品是否存在
        UpdateWrapper<Cart> wrapper = new UpdateWrapper<>();
        wrapper
                .eq("user_id", userId)
                .set("selected", selected);
        boolean update = this.update(wrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return this.cartList(userId);
    }
}




