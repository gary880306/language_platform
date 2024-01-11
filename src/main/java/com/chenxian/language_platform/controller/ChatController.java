package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.entity.ChatMessage;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.ChatService;
import com.chenxian.language_platform.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;

    @GetMapping("/enjoyLearning/chatroom")
    public String chatroom(Model model,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user",user);
        return "chatroom/main";
    }

    // 消息
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // 如果前端发送的消息对象属性名为message，则将其设置到ChatMessage的message属性中
        chatMessage.setMessage(chatMessage.getMessage());
        return chatMessage;
    }


    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
