package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理員用戶管理 REST API (SRP: 只負責用戶管理操作)
 * (DIP: 依賴 UserService 介面)
 * (LSP: 使用修正後的方法名 findAllNonAdminUsers / updateUserStatus)
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

    @Autowired
    private UserService userService;

    /** 取得所有非管理員用戶 */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllNonAdminUsers();
        if (users != null) {
            users.forEach(u -> u.setPassword(null));
        }
        return ResponseEntity.ok(users != null ? users : List.of());
    }

    /** 更新用戶停用/啟用狀態 */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer userId,
                                               @RequestBody Map<String, Boolean> body) {
        Boolean isActive = body.get("isActive");
        if (isActive == null) return ResponseEntity.badRequest().body(Map.of("message", "isActive 不可為空"));
        userService.updateUserStatus(userId, isActive);
        return ResponseEntity.ok(Map.of("message", "用戶狀態已更新"));
    }
}
