package com.network.network.messages.chatroom;

import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepo extends JpaRepository<ChatRoom, String> {
    
    // finds a chat room by a certain sender & receiver
    Optional<ChatRoom> findBySenderIdAndReceiverId(User senderId, User receiverId);

}