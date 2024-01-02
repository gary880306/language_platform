package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FrontendController {
    @Autowired
    private FrontendService frontendService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DataService dataService;

    @Autowired
    private UserService userService;
    @Autowired
    private CouponService couponService;

    // 顯示主頁 main 資訊頁面
    @GetMapping("/enjoyLearning/courses")
    public String showCourses(Model model,
                              // 查詢條件
                              @RequestParam(required = false) String search,
                              // 排序
                              @RequestParam(defaultValue = "created_date") String orderBy,
                              @RequestParam(defaultValue = "desc") String sort
                              // 分頁
                                ) {
        CourseQueryParams courseQueryParams = new CourseQueryParams();
        courseQueryParams.setSearch(search);
        courseQueryParams.setOrderBy(orderBy);
        courseQueryParams.setSort(sort);
        List<Course> courses = frontendService.getAllCourses(courseQueryParams);
        List<CategoryData> categories = dataService.findAllCategoryData();
        model.addAttribute("courses", courses);
        model.addAttribute("categories",categories);
        return "user/courses/main";
    }

    // 顯示商品資訊 courseInfo 頁面
    @GetMapping("/enjoyLearning/courses/courseInfo/{courseId}")
    public String getCourseInfo(@PathVariable("courseId") Integer courseId,HttpSession session,Model model){
        User user = (User) session.getAttribute("user");
        if (user != null) {
        boolean hasPurchased = courseService.hasUserPurchasedCourse(user.getUserId(), courseId);
        if (hasPurchased) {
                // 如果用戶已購買該課程，則導向課程主頁面
                CourseRequest course = courseService.getCourseById(courseId);
                model.addAttribute("courseId", courseId);
                model.addAttribute("course", course);
                return "/user/courses/courseMain";
            }
        }

        CourseRequest course = courseService.getCourseById(courseId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("course", course);
        return "/user/courses/courseInfo";
    }

    // 添加至購物車完成 addCart 頁面
    @PostMapping("/enjoyLearning/courses/addToCart")
    public String addToCart(@RequestParam("courseId") Integer courseId,
                            HttpSession session, Model model){
        // 1. 先找到 user 登入者
        User user = (User)session.getAttribute("user");

        // 2. 找到 user 的尚未結帳的購物車
        Cart cart = null;
        cart = userService.findNoneCheckoutCartByUserId(user.getUserId());

        if(cart == null){
            cart = new Cart(); // 建立新的購物車
            cart.setUserId(user.getUserId());
            userService.addCart(cart); // 將購物車存放到資料表

            // 新增之後馬上又要查詢, 建議可以下達一個 delay
            try {
                Thread.sleep(10);
            } catch (Exception e) {

            }

            // 再抓一次該使用者的購物車(目的是要得到剛才新增 cart 的 cartId)
            cart = userService.findNoneCheckoutCartByUserId(user.getUserId());

        }

        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getCartId());
        cartItem.setCourseId(courseId);

        userService.addCartItem(cartItem);

        model.addAttribute("course", courseService.getCourseById(courseId)); // 商品物件

        return "/user/courses/addCart";
    }

    // 購物車頁面
    @GetMapping("/enjoyLearning/cart")
    public String cartPage(HttpSession session, Model model) {
        // 1. 先找到 user 登入者
        User user = (User)session.getAttribute("user");
        // 2. 找到 user 的尚未結帳的購物車
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());
        if(cart != null){
            int total =  cart.getCartItems().stream().
                    mapToInt(item ->  item.getCourse().getPrice()).sum();
            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
        }

        return "/user/courses/cart";
    }

    // 刪除購物車項目
    @GetMapping("/enjoyLearning/cart/delete")
    public String deleteCartItem(@RequestParam("itemId") Integer itemId,
                                 HttpSession session, Model model) {
        User user = (User)session.getAttribute("user");
        // 如何得知 itemId 是屬於該使用者的 ?

        CartItem cartItem = userService.findCartItemById(itemId);
        if(cartItem.getCart().getUserId().equals(user.getUserId())){
            userService.removeCartItemById(itemId);
        }

        return "redirect:/enjoyLearning/cart";

    }

    // 購物車結帳
    @GetMapping("/enjoyLearning/checkout")
    public String checkout(HttpSession session, Model model) {
        // 1. 先找到 user 登入者
        User user = (User)session.getAttribute("user");
        // 2. 找到 user 的尚未結帳的購物車
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());


        if(cart != null){
            int total =  cart.getCartItems().stream().
                    mapToInt(item ->  item.getCourse().getPrice()).sum();
            userService.checkoutCartByUserId(cart.getUserId(),cart.getCartId()); // 結帳
            model.addAttribute("cart", cart);
            model.addAttribute("total", total);

        }

        return "/user/courses/purchasedResult";
    }

    // 我的課程頁面
    @GetMapping("/enjoyLearning/myCourse")
    public String myCourse(HttpSession session, Model model){
        // 1. 先找到 user 登入者
        User user = (User)session.getAttribute("user");
        List<UserCourse> userCourse = courseService.getPurchasedCourses(user.getUserId());
        List<Course> courses = new ArrayList<>();
        for (UserCourse uc : userCourse) {
            Course course = courseService.getCoursesByIdForCart(uc.getCourseId());
            courses.add(course);
        }

        model.addAttribute("user", user);
        model.addAttribute("courses",courses);
        return  "/user/courses/myCourse";
    }

    // 優惠券  -------------------------------------------------------------------------------------
    // 獲取所有優惠劵頁面
    @GetMapping("/enjoyLearning/coupons")
    public String showCoupons(Model model) {
        List<Coupon> coupons = couponService.getAllCoupons();
        model.addAttribute("coupons", coupons);
        return "user/coupons/couponInfo";
    }

    // 用戶獲取優惠券
    @PostMapping("/enjoyLearning/coupons/addCoupon")
    @ResponseBody
    public ResponseEntity<?> addCoupon(@RequestParam("couponId") Integer couponId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Coupon coupon = couponService.getCouponById(couponId);
        if (coupon == null || coupon.getQuantity() <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("優惠券不可用"));
        }

        boolean couponExists = userService.checkCouponExists(user.getUserId(), couponId);
        if (!couponExists) {
            userService.addUserCoupon(user.getUserId(), couponId);
            userService.decrementCouponQuantity(couponId);
            int updatedQuantity = getCurrentCouponQuantity(couponId);
            return ResponseEntity
                    .ok(new CouponResponse("優惠券添加成功", updatedQuantity));
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageResponse("用戶已經擁有此優惠券"));
    }

    // MessageResponse class (add this as an inner class or a separate class)
    @Data
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }

    // 假設您有一個方法來獲取更新後的優惠券數量
    public int getCurrentCouponQuantity(Integer couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        return coupon != null ? coupon.getQuantity() : 0;
    }

    @Data
    public static class CouponResponse {
        private String message;
        private int quantity;
        public CouponResponse(String message, int quantity) {
            this.message = message;
            this.quantity = quantity;
        }
    }

    @GetMapping("/enjoyLearning/cart/myCoupons")
    @ResponseBody
    public ResponseEntity<?> getUserCoupons(HttpSession session) {
        try {
            // 假設用戶信息存儲在 session 中的 "user" 屬性中
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
            }
            Integer userId = user.getUserId(); // 或者任何獲取用戶ID的方法
            List<UserCoupon> userCoupons = userService.findUserCouponsByUserId(userId);
            return ResponseEntity.ok(userCoupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving coupons: " + e.getMessage());
        }
    }

    @PostMapping("/enjoyLearning/cart/myCoupons/applyCoupon")
    @ResponseBody
    public Map<String, Object> applyCoupon(@RequestParam("couponId") Integer couponId, HttpSession session) {
        User user = (User)session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        Coupon coupon = couponService.getCouponById(couponId);
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());

        // 驗證優惠券是否有效
            // && coupon.isValid()
                // 計算折扣後的總金額
                BigDecimal total = calculateDiscountedTotal(cart, coupon);

                response.put("total", total.toString());
                return response;

    }

    private BigDecimal calculateDiscountedTotal(Cart cart, Coupon coupon) {
        BigDecimal total = BigDecimal.valueOf(cart.getCartItems().stream().mapToInt(item -> item.getCourse().getPrice()).sum());

        switch(coupon.getDiscountType()) {
            case FIXED:
                // 直接從總金額中減去優惠券的固定折扣值
                total = total.subtract(BigDecimal.valueOf(coupon.getDiscountValue().doubleValue()));
                total = total.setScale(0, RoundingMode.HALF_UP);
                break;
            case PERCENTAGE:
                // 計算百分比折扣
                BigDecimal discountMultiplier = BigDecimal.valueOf(1 - (coupon.getDiscountValue().doubleValue() / 100.0));
                total = total.multiply(discountMultiplier);
                total = total.setScale(0, RoundingMode.HALF_UP);
                break;
        }

        return total.compareTo(BigDecimal.ZERO) > 0 ? total : BigDecimal.ZERO; // 確保總金額不會小於0
    }





}
