package com.network.network.messages.chatroom;

import com.network.network.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService{
    
    private final ChatRoomRepo ChatRoomRepo;

    // returns a chat room, if not exists it creates one.
    public Optional<String> getChatRoomId(
        User senderId, User receiverId,
        boolean createNewRoomIfNotExists
        ) {
        return ChatRoomRepo.findBySenderIdAndReceiverId(senderId, receiverId)
            .map(ChatRoom::getChatId)
            .or(() -> {
                if (createNewRoomIfNotExists) {
                    var chatId = createChat(senderId, receiverId);
                    return Optional.of(chatId);
                }
                return Optional.empty();
            });
    }


    private String createChat(User senderId, User receiverId) {
        
        var chatId = String.format("%s_%s", senderId, receiverId);
        
        // Creating two chat rooms

        // Sender chat room
        ChatRoom SR = ChatRoom.builder()
            .chatId(chatId)
            .senderId(senderId)
            .receiverId(receiverId)
            .build();

        // Receiver chat room
        ChatRoom RS = ChatRoom.builder()
            .chatId(chatId)
            .senderId(receiverId)
            .receiverId(senderId)
            .build();


        // Save to repository Sender and Receiver chat rooms.
        ChatRoomRepo.save(SR);
        ChatRoomRepo.save(RS);

        return chatId;
    }

}
