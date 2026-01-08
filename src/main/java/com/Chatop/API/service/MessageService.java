package com.Chatop.API.service;

import com.Chatop.API.model.Message;
import com.Chatop.API.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
