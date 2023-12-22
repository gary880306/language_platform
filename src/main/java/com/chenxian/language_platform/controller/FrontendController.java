package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.dto.CourseQueryParams;
import com.chenxian.language_platform.dto.CourseRequest;
import com.chenxian.language_platform.model.*;
import com.chenxian.language_platform.service.CourseService;
import com.chenxian.language_platform.service.DataService;
import com.chenxian.language_platform.service.FrontendService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String getCourseInfo(@PathVariable("courseId") Integer courseId,Model model){
        CourseRequest course = courseService.getCourseById(courseId);
        System.out.println(course.getImageUrl());
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
        System.out.println(cart);

        if(cart != null){
            int total =  cart.getCartItems().stream().
                    mapToInt(item ->  item.getCourse().getPrice()).sum();
            model.addAttribute("cart", cart);
            model.addAttribute("total", total);
        }

        return "/user/courses/cart";
    }

}
