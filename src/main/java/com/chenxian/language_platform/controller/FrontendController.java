package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.entity.Comment;
import com.chenxian.language_platform.entity.Language;
import com.chenxian.language_platform.entity.Like;
import com.chenxian.language_platform.entity.Post;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.repository.CommentRepository;
import com.chenxian.language_platform.repository.LanguageRepository;
import com.chenxian.language_platform.repository.LikeRepository;
import com.chenxian.language_platform.repository.PostRepository;
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
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private LikeRepository likeRepository;


    // 顯示主頁 main 資訊頁面
    @GetMapping("/enjoyLearning/courses")
    public String showCourses(Model model,
                              HttpSession session,
                              // 查詢條件
                              @RequestParam(required = false) String search,
                              // 排序
                              @RequestParam(defaultValue = "created_date") String orderBy,
                              @RequestParam(defaultValue = "desc") String sort
                              // 分頁
                                ) {
        User user = (User) session.getAttribute("user");
        // 獲取用戶購物車中的課程數量
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        CourseQueryParams courseQueryParams = new CourseQueryParams();
        courseQueryParams.setSearch(search);
        courseQueryParams.setOrderBy(orderBy);
        courseQueryParams.setSort(sort);
        List<Course> courses = frontendService.getAllCourses(courseQueryParams);
        List<CategoryData> categories = dataService.findAllCategoryData();
        model.addAttribute("cartCourseCount",cartCourseCount);
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
        // 獲取用戶購物車中的課程數量
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        CourseRequest course = courseService.getCourseById(courseId);
        model.addAttribute("cartCourseCount",cartCourseCount);
        model.addAttribute("courseId", courseId);
        model.addAttribute("course", course);
        return "/user/courses/courseInfo";
    }


    // 檢查購物車是否有此課程 (courseInfo 加入購物車按鈕顯示已存在購物車)
    @GetMapping("/enjoyLearning/courses/checkCart")
    @ResponseBody
    public boolean isCourseInCart(@RequestParam("courseId") Integer courseId, HttpSession session) {
        User user = (User) session.getAttribute("user"); // 獲取當前登錄的用戶
        if (user == null) {
            return false; // 如果沒有用戶登錄，返回 false
        }

        // 假設 userService 有一個方法可以檢查購物車
        return userService.isCourseInUserCart(user.getUserId(), courseId);
    }


    // 添加至購物車完成 addCart 頁面
    // AJAX
    @PostMapping("/enjoyLearning/courses/addToCart")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam("courseId") Integer courseId,
                                       HttpSession session) {
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
        Integer updatedCartCount = userService.getCartCourseCount(user.getUserId());
        return ResponseEntity.ok().body(updatedCartCount);
    }

    // 購物車頁面
    @GetMapping("/enjoyLearning/cart")
    public String cartPage(HttpSession session, Model model) {
        // 1. 先找到 user 登入者
        User user = (User)session.getAttribute("user");
        // 2. 找到 user 的尚未結帳的購物車
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());
        if(cart != null){
            Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
            int total =  cart.getCartItems().stream().
                    mapToInt(item ->  item.getCourse().getPrice()).sum();
            model.addAttribute("cartCourseCount",cartCourseCount);
            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
        }

        return "/user/courses/cart";
    }

    // 刪除購物車項目
    // AJAX
    @DeleteMapping("/enjoyLearning/cart/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@RequestParam("itemId") Integer itemId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        CartItem cartItem = userService.findCartItemById(itemId);

        if(cartItem != null && cartItem.getCart().getUserId().equals(user.getUserId())) {
            userService.removeCartItemById(itemId);
            Integer updatedCartCount = userService.getCartCourseCount(user.getUserId());
            return ResponseEntity.ok().body(updatedCartCount);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權刪除此項目");
        }
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
        // 獲取用戶購物車中的課程數量
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());

        List<UserCourse> userCourse = courseService.getPurchasedCourses(user.getUserId());
        List<Course> courses = new ArrayList<>();
        for (UserCourse uc : userCourse) {
            Course course = courseService.getCoursesByIdForCart(uc.getCourseId());
            courses.add(course);
        }
        model.addAttribute("cartCourseCount",cartCourseCount);
        model.addAttribute("user", user);
        model.addAttribute("courses",courses);
        return  "/user/courses/myCourse";
    }

    // 優惠券  -------------------------------------------------------------------------------------
    // 獲取所有優惠劵頁面
    @GetMapping("/enjoyLearning/coupons")
    public String showCoupons(Model model,HttpSession session) {
        User user = (User)session.getAttribute("user");
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        List<Coupon> coupons = couponService.getAllCoupons();
        model.addAttribute("cartCourseCount",cartCourseCount);
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


    @GetMapping("/enjoyLearning/forum")
    public String showForum(Model model, HttpSession session) {
        List<Post> posts = postRepository.findByIsDeletedFalseOrderByCreateTimeDesc();
        User user = (User)session.getAttribute("user");
        List<Integer> likes = new ArrayList<>();
            // 檢查當前用戶對每個貼文的點讚狀態
            if (user != null) {
                for (Post post : posts) {
                    boolean liked = likeRepository.existsByUser_UserIdAndPost_Id(user.getUserId(), post.getId());
                    post.setLiked(liked);
                    Integer likesCount = likeRepository.countByPost(post);
                    likes.add(likesCount);
                }
            }

        List<Comment> comments = commentRepository.findAllByOrderByCreateTimeDesc();
        List<Language> languages = languageRepository.findAll(); // 獲取語言列表

        // 獲取用戶購物車中的課程數量
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        model.addAttribute("posts", posts);
        model.addAttribute("comments", comments);
        model.addAttribute("languages", languages); // 添加語言列表到模型中
        model.addAttribute("user", user);
        model.addAttribute("checkedUserId", user.getUserId());
        model.addAttribute("cartCourseCount",cartCourseCount);
        model.addAttribute("likes",likes);
        return "user/forum/main"; // 返回視圖的名稱
    }


}
