package com.chenxian.language_platform.service;

import com.chenxian.language_platform.dto.UserRegisterRequest;
import com.chenxian.language_platform.model.User;

/**
 * 認證服務介面 (SRP: 只負責認證相關邏輯)
 * (ISP: 使用者不需依賴不相關的購物車、個人資料方法)
 */
public interface AuthService {

    /** 註冊新用戶，回傳新用戶 ID */
    Integer register(UserRegisterRequest userRegisterRequest);

    /** 依 Email 查詢用戶（用於登入驗證） */
    User getUserByEmail(String email);

    /** 產生密碼重設 Token */
    String generateResetToken(String email);

    /** 以 Token 重設密碼 */
    boolean resetPasswordWithToken(String token, String newPassword) throws Exception;

    /** 驗證 Token 是否有效 */
    boolean checkTokenValidity(String token);
}
