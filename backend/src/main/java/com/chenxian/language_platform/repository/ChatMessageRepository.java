package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 自定義查詢 (如果需要)
}
