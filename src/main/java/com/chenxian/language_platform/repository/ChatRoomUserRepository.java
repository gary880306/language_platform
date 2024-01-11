package com.chenxian.language_platform.repository;

import com.chenxian.language_platform.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    // 自定義查詢 (如果需要)
}

