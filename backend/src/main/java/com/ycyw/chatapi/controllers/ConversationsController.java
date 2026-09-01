package com.ycyw.chatapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.mappers.MessageMapper;
import com.ycyw.chatapi.repositories.MessageRepository;

@RestController
@RequestMapping("/api/conversations")
public class ConversationsController {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public ConversationsController(MessageRepository messageRepository, MessageMapper messageMapper) {
      this.messageRepository = messageRepository;
      this.messageMapper = messageMapper;
    }

    @GetMapping("/{conversationId}/messages")
    public List<MessageDto> getHistory(@PathVariable String conversationId) {
      return messageRepository.
        findByConversationIdOrderBySentAtAsc(conversationId).
        stream().
        map(messageMapper::toDto).
        toList();
    }
}
