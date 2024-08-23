package com.network.network.messages.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// finds all chat messages with a specific id
public interface ChatMessagesRepository extends JpaRepository<ChatMessages, String> {
    List<ChatMessages> findByChatId(String chatId);
}