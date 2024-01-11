package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 自定義查詢 (如果需要)
}

