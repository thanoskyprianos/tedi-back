package com.network.network.messages.chat;

import com.network.network.messages.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    
    private final ChatMessagesRepository repository;
    private final ChatRoomService chatRoomService;

    // Saves a chat message to the repo
    // steps -> Retrieves chat room ID, sets retrieved id to chat messages.
    // saves message to repo
    public ChatMessages save(ChatMessages chatMessages) {
        
        var chatId = chatRoomService.getChatRoomId(
                chatMessages.getSenderId(), chatMessages.getReceiverId(), true)
                .orElseThrow();
    
        chatMessages.setChatId(chatId);
        repository.save(chatMessages);
    
        return chatMessages;
    
    }

    // Retrieves chat messages.
    // if sender and receiver do not have a chat room: false is returned.
    public List<ChatMessages> findChatMessages(
            String senderId, String receiverId
    ) {
        var chatId = chatRoomService.getChatRoomId(
                senderId, receiverId, false);

        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    
    }

}
