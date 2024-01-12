package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.entity.ChatMessage;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.ChatService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/enjoyLearning/chatroom/main")
    public String chatroom(@RequestParam(required = false) Long roomId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("roomId", roomId); // 添加 roomId 到模型
        return "chatroom/main";
    }

    // 消息
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, @Header("roomId") Long roomId) {
        // 构建目的地地址
        String destination = "/topic/chatroom/" + roomId;

        // 处理消息发送逻辑
        // 可以在这里保存消息到数据库
        chatMessage.setMessage(chatMessage.getMessage());
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

    // 修改后的 addUser 方法，使用 SimpMessageHeaderAccessor 管理会话
    @MessageMapping("/chat/{roomId}/addUser")
    @SendTo("/topic/chatroom/{roomId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable Long roomId) {
        String username = chatMessage.getSender();
        headerAccessor.getSessionAttributes().put("username", username);
        headerAccessor.getSessionAttributes().put("joinedRoomId", roomId); // 在 headerAccessor 中记录加入的房间 ID
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        // 这里可以处理用户加入聊天室的逻辑
        return chatMessage;
    }

    // 修改后的 leaveUser 方法，同样使用 SimpMessageHeaderAccessor
    @MessageMapping("/chat/{roomId}/leaveUser")
    @SendTo("/topic/chatroom/{roomId}")
    public ChatMessage leaveUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, @DestinationVariable Long roomId) {
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        // 清理 headerAccessor 中的会话属性
        headerAccessor.getSessionAttributes().remove("username");
        headerAccessor.getSessionAttributes().remove("joinedRoomId");
        return chatMessage;
    }

    // 检查用户是否已加入聊天室
    @GetMapping("/api/chat/checkStatus")
    public ResponseEntity<Boolean> checkUserStatus(HttpSession session) {
        Long roomId = (Long) session.getAttribute("joinedRoomId");
        return ResponseEntity.ok(roomId != null);
    }
}
