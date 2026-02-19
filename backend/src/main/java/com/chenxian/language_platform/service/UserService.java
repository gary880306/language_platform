package com.chenxian.language_platform.service;

import com.chenxian.language_platform.model.User;

import java.util.List;

/**
 * 用戶個人資料服務介面 (SRP: 只負責用戶個人資料管理)
 * (ISP: 認證邏輯移至 AuthService；購物車邏輯移至 CartService)
 * (LSP: 修正方法命名 findALlUsers → findAllUsers)
 */
public interface UserService {

    /** 查詢所有用戶（含管理員） */
    List<User> findAllUsers();

    /** 查詢所有非管理員用戶 */
    List<User> findAllNonAdminUsers();

    /** 依 ID 查詢用戶（不含 is_active 狀態篩選） */
    User getUserById(Integer userId);

    /** 依 ID 查詢用戶（含啟用狀態） */
    User getUserIncludeActiveById(Integer userId);

    /** 更新用戶基本資料（透過 JPA，含所有欄位） */
    User updateUser(User user);

    /** 更新用戶啟用/停用狀態 */
    boolean updateUserStatus(Integer userId, boolean isActive);
}
