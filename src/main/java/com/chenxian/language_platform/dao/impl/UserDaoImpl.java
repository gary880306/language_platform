package com.chenxian.language_platform.dao.impl;

import com.chenxian.language_platform.dao.CourseDao;
import com.chenxian.language_platform.dao.UserDao;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.rowmapper.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class UserDaoImpl implements UserDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private CourseDao courseDao;

    @Override
    public List<User> findALlUsers() {
        String sql = "SELECT user_id,email,password,user_name,birth,phone_number,address,created_date,last_modified_date,levelId,is_active,authType,authId FROM user";
        Map<String,Object> map = new HashMap<>();
        List<User> users = namedParameterJdbcTemplate.query(sql,map,new UserInfoRowMapper());
        if(!users.isEmpty()){

            return users;
        }
        return null;
    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO user(email,password,user_name,birth,phone_number,address,created_date,last_modified_date,authType,authId) " +
                "VALUES(:email,:password,:user_name,:birth,:phone_number,:address,:created_date,:last_modified_date,:authType,:authId)";
        Map<String,Object> map = new HashMap<>();
        map.put("email",userRegisterRequest.getEmail());
        map.put("password",userRegisterRequest.getPassword());
        map.put("user_name",userRegisterRequest.getUserName());
        map.put("birth",userRegisterRequest.getBirth());
        map.put("phone_number",userRegisterRequest.getPhoneNumber());
        map.put("address",userRegisterRequest.getAddress());
        map.put("authType",userRegisterRequest.getAuthType());
        map.put("authId",userRegisterRequest.getAuthId());

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
        String sql = "SELECT user_id,email,password,user_name,birth,phone_number,address,created_date,last_modified_date,levelId,is_active,authType,authId " +
                "FROM user WHERE user_id=:userId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        List<User> userList = namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());

        if(!userList.isEmpty()){
            return userList.get(0);
        }else{
            return null;
        }
    }

    @Override
    public User getUserIncludeActiveById(Integer userId) {
        String sql = "SELECT user_id,email,password,user_name,birth,phone_number,address,created_date,last_modified_date,levelId,is_active,authType,authId " +
                "FROM user WHERE user_id=:userId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        List<User> users = namedParameterJdbcTemplate.query(sql,map,new UserInfoRowMapper());
        if(!users.isEmpty()){
           return  users.get(0);
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id,email,password,user_name,birth,phone_number,address,created_date,last_modified_date,levelId,is_active,authType,authId FROM user WHERE email=:email";
        Map<String,Object> map = new HashMap<>();
        map.put("email",email);
        List<User> users = namedParameterJdbcTemplate.query(sql,map,new UserRowMapper());
        if(!users.isEmpty()){
            return users.get(0);
        }
            return null;
    }

    @Override
    public void updateUserInfo(User user) {
        String sql = "UPDATE user SET is_active = :isActive WHERE user_id = :userId";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getUserId());
        map.put("isActive",user.isActive());
        namedParameterJdbcTemplate.update(sql,map);
    }

    @Override
    public boolean isCourseInUserCart(Integer userId, Integer courseId) {
        String sql = "SELECT COUNT(*) FROM cart_item JOIN cart ON cart.cart_id = cart_item.cart_id WHERE cart.user_id = :userId AND cart_item.course_id = :courseId AND cart.isCheckout = FALSE";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("courseId", courseId);

        try {
            int count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);
            return count > 0;
        } catch (DataAccessException e) {
            // 處理異常，例如記錄日誌或傳回預設值
            // 日誌記錄等
            return false;
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
    public CartItem findCartItemById(Integer itemId) {
        String sql = "SELECT item_id, cart_id, course_id,created_date,last_modified_date FROM cart_item WHERE item_id = :itemId";
        Map<String,Object> map = new HashMap<>();
        map.put("itemId",itemId);
        List<CartItem> cartItemsList = namedParameterJdbcTemplate.query(sql,map,new CartItemRowMapper());
        if(cartItemsList.size() == 0){
            return null ;
        }
        CartItem cartItem = cartItemsList.get(0);
        cartItem.setCart(findCartById(cartItem.getCartId()));
        return cartItem;

    }

    @Override
    public List<CartItem> findCartItemsById(Integer cartId) {
        String sql = "SELECT item_id,cart_id,course_id,created_date,last_modified_date  FROM cart_item WHERE cart_id = :cartId";
        Map<String,Object> map = new HashMap<>();
        map.put("cartId",cartId);
        List<CartItem> cartItems = namedParameterJdbcTemplate.query(sql,map,new CartItemRowMapper());
        return cartItems;
    }

    @Override
    public Boolean removeCartItemById(Integer itemId) {
        String sql = "DELETE FROM cart_item WHERE item_id = :itemId";
        Map<String,Object> map = new HashMap<>();
        map.put("itemId",itemId);
        return namedParameterJdbcTemplate.update(sql,map) == 1;

    }

    @Override
    public Cart findCartById(Integer cartId) {
        String sql = "SELECT cart_id, user_id, isCheckout, created_date ,checkout_date FROM cart WHERE cart_id = :cartId";
        Map<String,Object> map = new HashMap<>();
        map.put("cartId",cartId);
        List<Cart> cartList = namedParameterJdbcTemplate.query(sql,map,new CartRowMapper());
        if(cartList.size() > 0){
            return cartList.get(0);
        }
        return null;
    }

    // 結帳
    @Transactional
    @Override
    public Boolean checkoutCartByUserId(Integer userId,Integer cartId) {
        // 1. 檢索購物車項目
        List<CartItem> cartItems = findCartItemsById(cartId);

        if (cartItems.isEmpty()) {
            return false;
        }
        // 2. 創建新訂單
        Order order = createOrder(userId, cartItems);

        // 3. 創建新訂單明細
        createOrderItems(userId,order.getOrderId(),cartItems);

        // 4. 將訂單中的課程加入成員擁有的課程清單
        createUserCourse(userId,cartItems);

        String sql = "UPDATE cart SET isCheckout = true WHERE user_id = :userId AND (isCheckout = false or isCheckout is null)";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);

        return namedParameterJdbcTemplate.update(sql,map) == 1;

    }

    // 創建訂單方法
    @Override
    public Order createOrder(Integer userId, List<CartItem> cartItems) {
        int totalAmount = 0;
        for (CartItem cartItem : cartItems){
            int amount =  courseDao.getCourseById(cartItem.getCourseId()).getPrice();
            totalAmount += amount;
        }
        // 創建新訂單
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);

        String sql = "INSERT INTO `order` (user_id, total_amount) VALUES (:userId, :totalAmount)";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("totalAmount",totalAmount);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql,new MapSqlParameterSource(map),keyHolder);
        Integer newOrderId = keyHolder.getKey().intValue();
        order.setOrderId(newOrderId);
        return order;
    }

    // 創建訂單明細方法
    @Override
    public void createOrderItems(Integer userId,Integer orderId, List<CartItem> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        int amount = 0;
        int courseId;
        for (CartItem cartItem : cartItems){
            amount =  courseDao.getCourseById(cartItem.getCourseId()).getPrice();
            courseId = cartItem.getCourseId();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setCourseId(courseId);
            orderItem.setAmount(amount);

            orderItems.add(orderItem);
        }

        for(OrderItem orderItem : orderItems){
            String sql = "INSERT INTO order_item(order_id,course_id,amount) VALUES (:orderId,:courseId,:amount)";
            Map<String,Object> map = new HashMap<>();
            map.put("orderId",orderId);
            map.put("courseId",orderItem.getCourseId());
            map.put("amount",orderItem.getAmount());


            namedParameterJdbcTemplate.update(sql,map);
        }


    }

    // 紀錄用戶擁有的課程清單
    @Override
    public void createUserCourse(Integer userId, List<CartItem> cartItems) {
        List<UserCourse> userCourses = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            int courseId = cartItem.getCourseId();
            UserCourse userCourse = new UserCourse();
            userCourse.setCourseId(courseId);
            userCourse.setUserId(userId);
            userCourses.add(userCourse);
        }

        for (UserCourse userCourse : userCourses) {
            String sql = "INSERT INTO user_course(user_id,course_id) VALUES (:userId,:courseId)";
            Map<String, Object> map = new HashMap<>();
            map.put("userId",userId);
            map.put("courseId",userCourse.getCourseId());
            namedParameterJdbcTemplate.update(sql,map);
        }
    }

    // 購物車新增
    @Override
    public void addCart(Cart cart) {
        String sql = "INSERT INTO cart(user_id, isCheckout) VALUES(:userId, :isCheckout)";
        Map<String, Object> map = new HashMap<>();
        map.put("userId", cart.getUserId());
        map.put("isCheckout", cart.isCheckout());

        namedParameterJdbcTemplate.update(sql, map);
    }

    // 購物車明細新增
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

    @Override
    public boolean checkCouponExists(Integer userId, Integer couponId) {
        String sql = "SELECT COUNT(*) FROM user_coupon WHERE user_id = :userId AND coupon_id = :couponId";

        Map<String, Object> map = Map.of("userId", userId, "couponId", couponId);

        int count = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return count > 0;
    }

    @Override
    public void addUserCoupon(Integer userId, Integer couponId) {
        String sql = "INSERT INTO user_coupon (user_id, coupon_id) VALUES (:userId, :couponId)";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("couponId", couponId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<UserCoupon> findUserCouponsByUserId(Integer userId) {
        String sql = "SELECT uc.user_id, uc.coupon_id, uc.is_used, c.code, c.description, c.discount_type, c.discount_value," +
                " c.start_date, c.end_date, c.is_active" +
                " FROM user_coupon uc" +
                " LEFT JOIN coupon c ON uc.coupon_id = c.coupon_id" +
                " WHERE uc.user_id =:userId;";
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        List<UserCoupon> userCoupons = namedParameterJdbcTemplate.query(sql,map,new UserCouponRowMapper());
        return userCoupons;
    }

    @Override
    public void decrementCouponQuantity(Integer couponId) {
        String sql = "UPDATE coupon SET quantity = quantity - 1 WHERE coupon_id =:couponId AND quantity > 0";
        Map<String,Object> map = new HashMap<>();
        map.put("couponId",couponId);
        namedParameterJdbcTemplate.update(sql,map);
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

        // 根據 courseId 找到 course 並注入
        cartItems.forEach(cartItem -> {
            Course course = courseDao.getCoursesByIdForCart(cartItem.getCourseId()); // 假設這個方法返回相應的 Course 對象

            cartItem.setCourse(course);
        });

        // 將注入了 course 的 cartItems 設置到 cart 中
        cart.setCartItems(cartItems);

    }

}
