package com.chenxian.language_platform.dao;

import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.Order;
import com.chenxian.language_platform.model.User;

import java.util.List;

public interface UserDao {
    Integer createUser(UserRegisterRequest userRegisterRequest);

    User getUserById(Integer userId);

    User getUserByEmail(String email);

    // 根據使用者ID來查找其未結帳的購物車資料(單筆)
    Cart findNoneCheckoutCartByUserId(Integer userId);
    CartItem findCartItemById(Integer itemId);
    List<CartItem> findCartItemsById(Integer cartId);
    Boolean removeCartItemById(Integer itemId);
    Cart findCartById(Integer cartId);
    Boolean checkoutCartByUserId(Integer userId,Integer cartId);
    Order createOrder(Integer userId,List<CartItem> cartItems);
    // 新增購物車資料
    void addCart(Cart cart);

    void addCartItem(CartItem cartItem);

}
