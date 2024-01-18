package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.customize.CheckoutResponse;
import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.entity.Comment;
import com.chenxian.language_platform.entity.Language;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private OrderedInfoService orderedInfoService;


    @GetMapping("/enjoyLearning/courses")
    public String showCourses(Model model, HttpSession session,
                              @RequestParam(required = false) String search,
                              @RequestParam(defaultValue = "created_date") String orderBy,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "8") Integer size) {
        User user = (User) session.getAttribute("user");

        CourseQueryParams courseQueryParams = new CourseQueryParams();
        courseQueryParams.setSearch(search);
        courseQueryParams.setOrderBy(orderBy);
        courseQueryParams.setSort(sort);
        courseQueryParams.setPage(page);
        courseQueryParams.setSize(size);

        List<Course> courses;
        Map<Integer, Integer> courseUserCounts = frontendService.getCourseUserCounts();


        // 仅当需要根据用户数量排序时使用 getAllCoursesWithUserCount 方法
        if ("userCount".equals(orderBy)) {
            courses = frontendService.getAllCoursesWithUserCount(courseQueryParams);
            courses.forEach(course -> {
                String formattedPrice = formatCoursePrice(course.getPrice());
                course.setFormattedPrice(formattedPrice); // 假设Course类有一个方法来设置格式化后的价格
            });
        } else {
            courses = frontendService.getAllCourses(courseQueryParams);
            courses.forEach(course -> {
                String formattedPrice = formatCoursePrice(course.getPrice());
                course.setFormattedPrice(formattedPrice); // 假设Course类有一个方法来设置格式化后的价格
            });
        }
        List<Course> searchCourses = "userCount".equals(orderBy) ?
                frontendService.getAllCoursesWithUserCount(courseQueryParams) :
                frontendService.getAllCourses(courseQueryParams);
        boolean hasCourses = searchCourses != null && !searchCourses.isEmpty();

        courses = courses.stream()
                .peek(course -> course.setPrice((course.getPrice())))
                .collect(Collectors.toList());
        model.addAttribute("hasCourses", hasCourses);
        model.addAttribute("search", search);
        model.addAttribute("size", size);
        model.addAttribute("cartCourseCount", userService.getCartCourseCount(user.getUserId()));
        model.addAttribute("courses", courses);
        model.addAttribute("courseUserCounts", courseUserCounts); // 总是添加 courseUserCounts 到模型
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        Integer totalCourses = frontendService.getCoursesCount(courseQueryParams);
        Integer totalPages = (int) Math.ceil((double) totalCourses / size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categories", dataService.findAllCategoryData());

        return "user/courses/main";
    }




    // 顯示商品資訊 courseInfo 頁面
    @GetMapping("/enjoyLearning/courses/courseInfo/{courseId}")
    public String getCourseInfo(@PathVariable("courseId") Integer courseId, HttpSession session, Model model){
        User user = (User) session.getAttribute("user");
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        if (user != null) {
            boolean hasPurchased = courseService.hasUserPurchasedCourse(user.getUserId(), courseId);
            if (hasPurchased) {
                CourseRequest course = courseService.getCourseById(courseId);
                model.addAttribute("formattedPrice", formatCoursePrice(course.getPrice()));
                model.addAttribute("course", course);
                model.addAttribute("cartCourseCount", cartCourseCount);
                return "/user/courses/courseMain";
            }
        }
        CourseRequest course = courseService.getCourseById(courseId);
        Map<Integer, Integer> courseUserCounts = frontendService.getCourseUserCounts();
        model.addAttribute("courseUserCounts", courseUserCounts);
        model.addAttribute("cartCourseCount", cartCourseCount);
        model.addAttribute("formattedPrice", formatCoursePrice(course.getPrice()));
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
        User user = (User) session.getAttribute("user");
        // 2. 找到 user 的尚未結帳的購物車
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        if(cart != null) {
            cartCourseCount = userService.getCartCourseCount(user.getUserId());
            Integer total = cart.getCartItems().stream()
                    .mapToInt(item -> item.getCourse().getPrice()).sum();
            // 检查是否应用了优惠券
            if (cart.getCouponId() != null) {
                Coupon coupon = couponService.getCouponById(cart.getCouponId());
                if (coupon != null) {
                    // 计算优惠后的总额
                    BigDecimal discount = calculateDiscountAmount(cart, coupon);
                    BigDecimal discountedTotal = new BigDecimal(total).subtract(discount);

                    model.addAttribute("discount", discount);
                    model.addAttribute("discountedTotal", discountedTotal);
                }
            }
                model.addAttribute("cartCourseCount", cartCourseCount);
                model.addAttribute("total", total);

            model.addAttribute("cart", cart);
        }
        model.addAttribute("cartCourseCount", cartCourseCount);
        return "/user/courses/cart";
    }

    // 刪除購物車項目
    // AJAX
    @DeleteMapping("/enjoyLearning/cart/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@RequestParam("itemId") Integer itemId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        CartItem cartItem = userService.findCartItemById(itemId);

        if (cartItem != null && cartItem.getCart().getUserId().equals(user.getUserId())) {
            userService.removeCartItemById(itemId);

            // 检查购物车是否为空
            Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());
            if (cart != null && cart.getCartItems().isEmpty()) {
                // 如果购物车为空，设置 couponId 为 null
                userService.updateCartCoupon(null, cart.getCartId());
            }

            Integer updatedCartCount = userService.getCartCourseCount(user.getUserId());
            return ResponseEntity.ok().body(updatedCartCount);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權刪除此項目");
        }
    }

    // 結帳頁面
    @GetMapping("/enjoyLearning/checkout")
    public String checkout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // 1. 先找到 user 登入者
        User user = (User) session.getAttribute("user");
        // 2. 找到 user 的尚未結帳的購物車
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());
        BigDecimal discount = BigDecimal.ZERO; // 初始化折扣金额为0

        if (cart != null) {
            Integer totalInteger = cart.getCartItems().stream()
                    .mapToInt(item -> item.getCourse().getPrice())
                    .sum();
            BigDecimal total = new BigDecimal(totalInteger);

            // 檢查 couponId 是否為0
            if (cart.getCouponId() != 0) {
                // 獲取當前購物車的 coupon
                Coupon coupon = couponService.getCouponById(cart.getCouponId());
                // 根據優惠券計算折扣金額
                discount = calculateDiscountAmount(cart, coupon);
                total = total.subtract(discount);
                // 將優惠券設置為已使用
                userService.markCouponAsUsed(user.getUserId(), cart.getCouponId());
            }

            // 轉換折扣金额为 Integer
            Integer discountInteger = discount.intValue();

            // 結帳
            CheckoutResponse checkoutResponse = userService.checkoutCartByUserId(cart.getUserId(), cart.getCartId(), discountInteger);
            if (checkoutResponse != null && checkoutResponse.getSuccess()) {
                // 添加訂單相關資訊到重定向屬性
                String formattedTotalAmount = formatCoursePrice(totalInteger);
                Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
                String formattedDiscount = formatCoursePrice(discountInteger);

                redirectAttributes.addAttribute("total", formattedTotalAmount);
                redirectAttributes.addAttribute("cartCourseCount", cartCourseCount);
                redirectAttributes.addAttribute("formattedDiscount", formattedDiscount);
                redirectAttributes.addAttribute("orderId", checkoutResponse.getOrderId());

                return "redirect:/enjoyLearning/orderResult";
            }
        }
        return "redirect:/enjoyLearning/checkoutError"; // 假設有錯誤處理頁面
    }

    @GetMapping("/enjoyLearning/orderResult")
    public String orderResult(@RequestParam("orderId") Integer orderId,
                              @RequestParam("total") String totalAmountStr, // 現在是String類型
                              @RequestParam("cartCourseCount") Integer cartCourseCount,
                              Model model) {
        String resetTotalAmountStr = totalAmountStr.replaceAll(",", ""); // 去除逗號
        Integer totalAmount = Integer.parseInt(resetTotalAmountStr); // 轉換為整數
        // 通過 orderId 獲取訂單信息
        Order order = orderedInfoService.findOrderById(orderId);
        Integer discount = 0;
        if (order != null) {
            discount = order.getDiscount(); // 從訂單中獲取折扣
        }

        List<OrderItem> orderItems = orderedInfoService.findOrderItemByOrderId(orderId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 格式化訂單項目數據
        for (OrderItem item : orderItems) {
            String formattedDate = dateFormat.format(item.getCreated_date());
            String formattedAmount = formatCoursePrice(item.getAmount());
            item.setFormattedAmount(formattedAmount);
            item.setFormattedCreatedDate(formattedDate);
        }

        // 計算並格式化折扣後的總金額
        Integer totalAmountInt = Integer.parseInt(String.valueOf(totalAmount)); // 將String轉換為Integer
        Integer discountedTotal = totalAmountInt - discount;
        String formattedTotal = formatCoursePrice(discountedTotal);

        // 將訂單信息添加到模型中
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("total", formattedTotal); // 格式化後的折扣後總金額
        model.addAttribute("cartCourseCount", cartCourseCount);
        model.addAttribute("formattedDiscount", formatCoursePrice(discount));

        return "/user/courses/orderResult";
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
    public String showCoupons(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        List<Coupon> allCoupons = couponService.getAllCoupons();

        // 過濾出 is_active 為 true 的優惠券
        List<Coupon> activeCoupons = allCoupons.stream()
                .filter(Coupon::isActive)
                .collect(Collectors.toList());

        model.addAttribute("cartCourseCount", cartCourseCount);
        model.addAttribute("coupons", activeCoupons); // 使用過濾後的優惠券列表
        return "user/coupons/couponInfo";
    }


    // 用戶獲取優惠券
    @PostMapping("/enjoyLearning/coupons/addCoupon")
    @ResponseBody
    public ResponseEntity<?> addCoupon(@RequestParam("couponId") Integer couponId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("用戶未登入"));
        }

        Coupon coupon = couponService.getCouponById(couponId);
        if (coupon == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("優惠券不存在"));
        }

        // 检查用户是否曾经领取过这个优惠券
        boolean couponExists = couponService.checkCouponExists(user.getUserId(), couponId);
        if (!couponExists) {
            // 用户完全没有领取过此优惠券，添加新的用户优惠券关联
            userService.addUserCoupon(user.getUserId(), couponId);
            userService.decrementCouponQuantity(couponId);
            int updatedQuantity = getCurrentCouponQuantity(couponId);
            return ResponseEntity.ok(new CouponResponse("優惠券添加成功", updatedQuantity));
        } else {
            // 用户之前已领取过此优惠券，检查是否已使用
            boolean isUsed = couponService.checkCouponExistsByIsUsed(user.getUserId(), couponId);
            if (!isUsed) {
                // 优惠券已经被使用，可以重新激活
                userService.updateUserCoupon(user.getUserId(), couponId);
                userService.decrementCouponQuantity(couponId);
                int updatedQuantity = getCurrentCouponQuantity(couponId);
                return ResponseEntity.ok(new CouponResponse("重新獲取成功", updatedQuantity));
            } else {
                // 用户已经拥有此优惠券且尚未使用，无需重复添加
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("用戶已擁有此優惠券"));
            }
        }
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

        // 查看購物車當前套用的優惠券
        @GetMapping("/enjoyLearning/cart/currentCoupon")
        @ResponseBody
        public ResponseEntity<?> getCurrentCoupon(HttpSession session) {
            try {
                User user = (User) session.getAttribute("user");
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
                }
                Integer userId = user.getUserId();
                Integer couponId = userService.getCurrentCouponIdByUserId(userId);

                Map<String, Object> response = new HashMap<>();
                response.put("couponId", couponId);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving current coupon: " + e.getMessage());
            }
        }



    // 購物車該用戶點選使用優惠券按鈕時獲取當前所擁有的優惠券
    @GetMapping("/enjoyLearning/cart/myCoupons")
    @ResponseBody
    public ResponseEntity<?> getUserCoupons(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
            }
            Integer userId = user.getUserId();
            List<UserCoupon> userCoupons = userService.findUnusedUserCouponsByUserId(userId);

            // 利用時間設置優惠券狀態
            Date now = new Date();
            for (UserCoupon userCoupon : userCoupons) {
                Coupon coupon = userCoupon.getCoupon();
                if (now.before(coupon.getStartDate())) {
                    userCoupon.setStatus("未開始");
                } else if (now.after(coupon.getEndDate())) {
                    userCoupon.setStatus("已過期");
                } else {
                    userCoupon.setStatus("有效");
                }
            }
            return ResponseEntity.ok(userCoupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving coupons: " + e.getMessage());
        }
    }

    @PostMapping("/enjoyLearning/cart/myCoupons/cancelCoupon")
    @ResponseBody
    public ResponseEntity<?> cancelCoupon(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
            }
            userService.cancelCoupon(user.getUserId());
            System.out.println("123");
            return ResponseEntity.ok().body("優惠券已取消");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("取消優惠券失敗: " + e.getMessage());
        }
    }




    @PostMapping("/enjoyLearning/cart/myCoupons/applyCoupon")
    @ResponseBody
    public Map<String, Object> applyCoupon(@RequestParam("couponId") Integer couponId, HttpSession session) {
        User user = (User)session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        Coupon coupon = couponService.getCouponById(couponId);
        Cart cart = userService.findNoneCheckoutCartByUserId(user.getUserId());
        couponService.applyCouponToCart(user.getUserId(),couponId);

        // 驗證優惠券是否有效

                // && coupon.isValid()

                // 計算折扣的金額
                BigDecimal discounted = calculateDiscountAmount(cart,coupon);

                // 計算折扣後的總金額
                BigDecimal total = calculateDiscountedTotal(cart, coupon);

                response.put("discounted",discounted.toString());
                response.put("total", total.toString());
                return response;

    }

    // 計算折扣的金額方法
    private BigDecimal calculateDiscountAmount(Cart cart, Coupon coupon) {
        BigDecimal total = BigDecimal.valueOf(cart.getCartItems().stream()
                .mapToInt(item -> item.getCourse().getPrice())
                .sum());
        BigDecimal discountAmount = BigDecimal.ZERO;

        switch(coupon.getDiscountType()) {
            case FIXED:
                // 固定值折扣
                discountAmount = BigDecimal.valueOf(coupon.getDiscountValue().doubleValue());
                break;
            case PERCENTAGE:
                // 百分比折扣
                BigDecimal discountRate = BigDecimal.valueOf(coupon.getDiscountValue().doubleValue() / 100.0);
                discountAmount = total.multiply(discountRate);
                break;
        }

        discountAmount = discountAmount.setScale(0, RoundingMode.HALF_UP);

        // 確保折扣金額不會大於原始總金額
        return discountAmount.compareTo(total) > 0 ? total : discountAmount;
    }

    // 計算折扣後的總金額方法
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


    private String formatCoursePrice(Integer price) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        return formatter.format(price);
    }

    @GetMapping("/enjoyLearning/user")
    private String showUserInfo(Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        Integer cartCourseCount = userService.getCartCourseCount(user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("cartCourseCount",cartCourseCount);
        return "user/personalInformation/main";
    }

}
