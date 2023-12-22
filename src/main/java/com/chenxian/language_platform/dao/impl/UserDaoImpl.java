package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.CourseDao;
import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.dto.UserLoginRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.Cart;
import com.chenxian.language_platform.model.CartItem;
import com.chenxian.language_platform.model.Course;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.rowmapper.CartItemRowMapper;
import com.chenxian.language_platform.rowmapper.CartRowMapper;
import com.chenxian.language_platform.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private CourseDao courseDao;
    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO user(email,password,user_name,birth,phone_number,address,created_date,last_modified_date) " +
                "VALUES(:email,:password,:user_name,:birth,:phone_number,:address,:created_date,:last_modified_date)";
        Map<String,Object> map = new HashMap<>();
        map.put("email",userRegisterRequest.getEmail());
        map.put("password",userRegisterRequest.getPassword());
        map.put("user_name",userRegisterRequest.getUserName());
        map.put("birth",userRegisterRequest.getBirth());
        map.put("phone_number",userRegisterRequest.getPhoneNumber());
        map.put("address",userRegisterRequest.getAddress());

        Date now = new Date();
        map.put("created_date",now);
        map.put("last_modified_date",now);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map),keyHolder);
        int userId = keyHolder.getKey().intValue();
        return userId;
    }

    @Override
    public User getUserById(Integer userId) {
        String sql = "SELECT user_id,email,password,user_name,birth,phone_number,address,created_date,last_modified_date " +
                "FROM user WHERE user_id=:userId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        List<User> userList = namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());
        if(userList.size() > 0){
            return userList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id,email,password,user_name,birth,phone_number,address,created_date,last_modified_date FROM user WHERE email=:email";
        Map<String,Object> map = new HashMap<>();
        map.put("email",email);
        List<User> userList = namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());
        if(userList.size() > 0){
            return userList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public Cart findNoneCheckoutCartByUserId(Integer userId) {
        String sql = "SELECT cart_id, user_id, isCheckout, created_date, checkout_date FROM cart " +
                "WHERE user_id = :userId AND (isCheckout = false OR isCheckout IS NULL)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        List<Cart> cartList = namedParameterJdbcTemplate.query(sql, map, new CartRowMapper());
        if(cartList.size() > 0){
            enrichCartWithDetails(cartList.get(0));
            return cartList.get(0);
        }else{
            return null;
        }

    }

    @Override
    public void addCart(Cart cart) {
        String sql = "INSERT INTO cart(user_id, isCheckout) VALUES(:userId, :isCheckout)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", cart.getUserId());
        map.put("isCheckout", cart.isCheckout());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        String sql1 = "SELECT COUNT(*) AS COUNT FROM cart_item WHERE cart_id = :cartId AND course_id = :courseId";

        Map<String, Object> map1 = new HashMap<>();
        map1.put("cartId", cartItem.getCartId());
        map1.put("courseId", cartItem.getCourseId());

        int count = namedParameterJdbcTemplate.queryForObject(sql1, map1, Integer.class);
        if (count == 0) {
            String sql2 = "INSERT INTO cart_item(cart_id, course_id) VALUES(:cartId,:courseId)";
            Map<String,Object> map2 = new HashMap<>();
            map2.put("cartId",cartItem.getCartId());
            map2.put("courseId", cartItem.getCourseId());
            namedParameterJdbcTemplate.update(sql2,map2);
        } else {
            throw new ItemAlreadyInCartException("商品已存在於購物車中");
        }
    }

    // 自定義異常類
    public class ItemAlreadyInCartException extends RuntimeException {
        public ItemAlreadyInCartException(String message) {
            super(message);
        }
    }

    // 為 cart 注入 cartItem
    // details: 使用者物件(user) 與 購物車明細(cartItems), 以及購物車明細的商品資料
    private void enrichCartWithDetails(Cart cart) {
        // 注入 user
        //findUserById(cart.getUserId()).ifPresent(user -> cart.setUser(user));
        User user =  getUserById(cart.getUserId());
        if(user != null){
            cart.setUser(user);
        }
        // 查詢 cartItems 並注入
        String sqlItems = "SELECT item_id, cart_id, course_id, created_date, last_modified_date FROM cart_item WHERE cart_id = :cartId";
        Map<String, Object> map = new HashMap<>();
        map.put("cartId", cart.getCartId());
        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sqlItems, map, new CartItemRowMapper());
        System.out.println("-----------------------------------");
        System.out.println(cartItems);
        // 根據 courseId 找到 course 並注入
        cartItems.forEach(cartItem -> {
            Course course = courseDao.getCoursesByIdForCart(cartItem.getCourseId()); // 假設這個方法返回相應的 Course 對象
            System.out.println("-----------------------------------");
            System.out.println(course);
            cartItem.setCourse(course);
        });

        // 將注入了 course 的 cartItems 設置到 cart 中
        cart.setCartItems(cartItems);

    }
}
