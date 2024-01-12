package com.chenxian.language_platform.service;

import com.chenxian.language_platform.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createChatRoom(String chatRoomName, Long ownerId);
    List<ChatRoom> getAllChatRooms();
}
