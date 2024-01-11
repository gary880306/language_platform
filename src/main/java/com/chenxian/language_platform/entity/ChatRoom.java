package com.chenxian.language_platform.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String chatRoomName;
    private Long ownerId;

    @OneToMany(mappedBy = "chatRoom")
    private Set<ChatMessage> messages;

}
