package com.network.network.messages.resource;

import com.network.network.messages.Message;
import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySenderAndRecipient(User sender, User recipient);
}
