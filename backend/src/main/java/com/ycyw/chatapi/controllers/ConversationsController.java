package com.ycyw.chatapi.controllers;

import java.util.List;

import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.services.ChatService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
public class ConversationsController {

    private final ChatService chatService;

    public ConversationsController(ChatService chatService) {
      this.chatService = chatService;
    }

    @GetMapping("/{conversationId}/messages")
    public List<MessageDto> getHistory(@PathVariable String conversationId) {
      return chatService.getConversationHistory(conversationId);
    }
}
