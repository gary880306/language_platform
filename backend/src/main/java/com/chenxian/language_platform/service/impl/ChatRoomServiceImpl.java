package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.entity.ChatRoom;
import com.chenxian.language_platform.repository.ChatRoomRepository;
import com.chenxian.language_platform.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(String chatRoomName, Long ownerId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomName(chatRoomName);
        chatRoom.setOwnerId(ownerId);
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    // 其他可能的方法，如查找聊天室、删除聊天室等
}