package com.chenxian.language_platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ChatRoomUser {
    @Id
    private Long chatRoomId;
    private Long userId;

}
