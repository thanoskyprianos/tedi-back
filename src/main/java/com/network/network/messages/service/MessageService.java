package com.network.network.messages.service;

import com.network.network.messages.Message;
import com.network.network.messages.resource.MessageRepository;
import com.network.network.user.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MessageService {
    @Resource
    private MessageRepository messageRepository;

    public List<Message> getAllMessages(User user1, User user2) {
        List<Message> messages1 = messageRepository.findBySenderAndRecipient(user1, user2);
        List<Message> messages2 = messageRepository.findBySenderAndRecipient(user2, user1);

        messages1.addAll(messages2);
        messages1.sort(Comparator.comparing(Message::getCreated));

        return messages1;
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }
}
