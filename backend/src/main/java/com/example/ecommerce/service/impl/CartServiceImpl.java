package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CartDTO> getUserCarts(Long userId) {
        List<Cart> carts = cartMapper.findByUserId(userId);
        return carts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        Cart existingCart = cartMapper.findByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            updateById(existingCart);
            return convertToDTO(existingCart);
        }

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setSelected(1);
        save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartQuantity(Long id, Long userId, Integer quantity) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }

        Product product = productMapper.selectById(cart.getProductId());
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        cart.setQuantity(quantity);
        updateById(cart);
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public boolean deleteCart(Long id, Long userId) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean selectCart(Long id, Long userId, Integer selected) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项不存在");
        }
        cart.setSelected(selected);
        return updateById(cart);
    }

    @Override
    @Transactional
    public boolean selectAllCart(Long userId, Integer selected) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> carts = list(wrapper);
        for (Cart cart : carts) {
            cart.setSelected(selected);
            updateById(cart);
        }
        return true;
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        BeanUtils.copyProperties(cart, dto);

        Product product = productMapper.selectById(cart.getProductId());
        if (product != null) {
            dto.setProductName(product.getName());
            dto.setProductImage(product.getMainImage());
            dto.setProductPrice(product.getPrice());
            dto.setStock(product.getStock());
        }

        return dto;
    }
}
