package com.network.network.messages.chat;

import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String chatId;

    @ManyToOne
    private User senderId;
    
    @ManyToOne
    private User receiverId;

    private String message;
    private Date date;
}
