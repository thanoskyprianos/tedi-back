// Representation of the chat room

package com.network.network.messages.chatroom;

import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String chatId;

    @ManyToOne
    private User senderId;
    
    @ManyToOne
    private User receiverId;

}
