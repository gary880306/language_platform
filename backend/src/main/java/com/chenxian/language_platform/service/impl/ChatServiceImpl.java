package com.chenxian.language_platform.service.impl;

import com.chenxian.language_platform.repository.ChatMessageRepository;
import com.chenxian.language_platform.repository.ChatRoomRepository;
import com.chenxian.language_platform.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;
}
