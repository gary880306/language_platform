package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.entity.ChatRoom;
import com.chenxian.language_platform.model.User;
import com.chenxian.language_platform.service.ChatRoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/enjoyLearning/chatroom")
    public String showRooms(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
        model.addAttribute("user",user);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("chatRooms", chatRooms);
        return "chatroom/createRooms";
    }

    @PostMapping("/chatroom/create")
    @ResponseBody
    public ResponseEntity<?> createChatRoom(@RequestParam String chatRoomName, @RequestParam Long ownerId) {
        ChatRoom createdChatRoom = chatRoomService.createChatRoom(chatRoomName, ownerId);
        return ResponseEntity.ok(createdChatRoom);
    }


    // 其他可能的控制器方法
}