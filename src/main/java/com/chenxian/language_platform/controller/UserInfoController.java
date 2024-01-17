package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserInfoController {
    @Autowired
    private UserService userService;
    @GetMapping("/admin/userInfo")
    public String showAllReport(Model model){
        List<User> users = userService.findAllNonAdminUsers();
        model.addAttribute("users",users);
        return "admin/userInfo";
    }

    @PostMapping("/admin/updateUserStatus")
    public ResponseEntity<?> updateUserStatus(@RequestParam Integer userId,
                                              @RequestParam Boolean isActive) {
        try {
            userService.updateUserActiveStatus(userId, isActive);
            return ResponseEntity.ok("User status updated successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user status: " + e.getMessage());
        }
    }

}
