package com.network.network.messages.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    // Send message to a user
    private final SimpMessagingTemplate messagingTemplate;
    
    // Idea for sending messages
    private final ChatMessageService chatMessageService;

    // Handling for WebSocket messages: Saves messages (chatMessageservice),
    // send notification to receiver using SimpMessagingTemplate.
    @MessageMapping
    public void processMessage(@Payload ChatMessages chatMessages) {

        ChatMessages savedMessage = chatMessageService.save(chatMessages);
        
        messagingTemplate.convertAndSendToUser(
            chatMessages.getReceiverId(), "queue/messages",
            ChatNotification.builder()
                .id(savedMessage.getId())
                .senderId(savedMessage.getSenderId())
                .receiverId(savedMessage.getReceiverId())
                .message(savedMessage.getMessage())
                .build()
        );
    
    }

    // HTTP request handling:
    // GET messages between sender and receiver,
    // returns list of those messages 
    @GetMapping("/messages/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatMessages>> findChatMessages(
            @PathVariable("senderId") String senderId,
            @PathVariable("receiverId") String receiverId
    ) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, receiverId));
    }
    
}
