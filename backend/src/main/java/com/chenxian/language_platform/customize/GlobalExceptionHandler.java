package com.chenxian.language_platform.customize;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 全域例外處理器 (SRP: 統一處理例外，控制器不需各自實作錯誤處理)
 * (OCP: 新增例外類型只需添加新的 @ExceptionHandler 方法，不需修改現有程式碼)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 處理業務邏輯例外（如購物車商品已存在） */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "操作失敗"));
    }

    /** 處理參數驗證失敗 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "參數錯誤"));
    }

    /** 處理未預期例外 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "伺服器發生錯誤，請稍後再試"));
    }
}
