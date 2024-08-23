// Representation of the chat room

package com.network.network.messages.chatroom;
import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {
    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;
}
