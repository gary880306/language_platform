package com.chenxian.language_platform.controller;

import com.chenxian.language_platform.entity.ChatMessage;
import com.chenxian.language_platform.entity.ChatRoom;
import com.chenxian.language_platform.service.ChatRoomService;
import com.chenxian.language_platform.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聊天室 REST API + WebSocket 訊息處理
 */
@RestController
public class ChatApiController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ==================== 聊天室管理 ====================

    /** 取得所有聊天室列表 */
    @GetMapping("/api/chatrooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms() {
        return ResponseEntity.ok(chatRoomService.getAllChatRooms());
    }

    /** 建立聊天室 */
    @PostMapping("/api/chatrooms")
    public ResponseEntity<?> createChatRoom(@RequestBody Map<String, Object> body,
                                             HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String chatRoomName = (String) body.get("chatRoomName");

        if (chatRoomName == null || chatRoomName.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "聊天室名稱不可為空"));
        }

        ChatRoom room = chatRoomService.createChatRoom(chatRoomName, userId.longValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    /** 取得聊天室歷史訊息 */
    @GetMapping("/api/chatrooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getMessagesByRoomId(roomId));
    }

    // ==================== WebSocket 訊息處理 ====================

    /** 傳送訊息 */
    @org.springframework.messaging.handler.annotation.MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @Header("roomId") Long roomId) {
        chatMessage.setChatRoomId(roomId);
        chatService.saveMessage(chatMessage);
        messagingTemplate.convertAndSend("/topic/chatroom/" + roomId, chatMessage);
    }

    /** 加入聊天室 */
    @org.springframework.messaging.handler.annotation.MessageMapping("/chat/{roomId}/addUser")
    @SendTo("/topic/chatroom/{roomId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor,
                               @DestinationVariable Long roomId) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("joinedRoomId", roomId);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatMessage;
    }

    /** 離開聊天室 */
    @org.springframework.messaging.handler.annotation.MessageMapping("/chat/{roomId}/leaveUser")
    @SendTo("/topic/chatroom/{roomId}")
    public ChatMessage leaveUser(@Payload ChatMessage chatMessage,
                                 SimpMessageHeaderAccessor headerAccessor,
                                 @DestinationVariable Long roomId) {
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        headerAccessor.getSessionAttributes().remove("username");
        headerAccessor.getSessionAttributes().remove("joinedRoomId");
        return chatMessage;
    }
}
