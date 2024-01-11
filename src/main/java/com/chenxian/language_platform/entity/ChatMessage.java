package com.chenxian.language_platform.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "chatRoomId", nullable = false)
    private ChatRoom chatRoom;

    private Long senderId;
    private String sender; // 添加 sender 属性
    private String message;

}
