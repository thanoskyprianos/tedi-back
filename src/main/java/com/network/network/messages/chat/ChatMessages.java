package com.network.network.messages.chat;

import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessages {
    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String message;
    private Date date;
}
