package com.network.network.messages;

import com.network.network.messages.service.MessageService;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MessagingController {
    @Resource
    private UserService userService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Resource
    private MessageService messageService;

    @MessageMapping("/message")
    public void sendMessage(@Payload MessageRepr message, Principal principal) {
        User sender = userService.getUserByEmail(principal.getName());
        User recipient = userService.getUserById(message.getRecipient());

        if (recipient.isConnected(sender)) return;

        Message messageObj = new Message(message.getMessage(), sender, recipient);
        messageService.saveMessage(messageObj);

        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue", messageObj);
        messagingTemplate.convertAndSendToUser(recipient.getEmail(), "/queue", messageObj);
    }

    @GetMapping("/messages/{friendId}")
    public ResponseEntity<?> getMessagesWithFriend(@PathVariable int friendId) {
        User principal = userService.getPrincipal();
        User friend = userService.getUserById(friendId);

        if (!principal.isConnected(friend)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }

        return ResponseEntity.ok(messageService.getAllMessages(principal, friend));
    }
}
