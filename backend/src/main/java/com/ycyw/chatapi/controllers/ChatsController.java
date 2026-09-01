package com.ycyw.chatapi.controllers;

import com.ycyw.chatapi.dtos.IncomingMessageDto;
import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.entities.Message;
import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.mappers.MessageMapper;
import com.ycyw.chatapi.repositories.MessageRepository;
import com.ycyw.chatapi.repositories.UserRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatsController {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  public ChatsController(
    MessageRepository messageRepository,
    UserRepository userRepository,
    SimpMessagingTemplate messagingTemplate,
    MessageMapper messageMapper
  ) {
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
    this.messagingTemplate = messagingTemplate;
    this.messageMapper = messageMapper;
  }

  @MessageMapping("/chat.send")
  public void send(@Payload @Valid IncomingMessageDto incoming, Principal principal) {
    if (principal == null || principal.getName() == null) {
      return;
    }

    User sender = userRepository.
      findByEmail(principal.getName()).
      orElseThrow(
        () -> new IllegalStateException("Authenticated user not found")
      );

    Message message = new Message().
      setConversationId(incoming.conversationId()).
      setSender(sender).
      setContent(incoming.content()).
      setSentAt(Instant.now());

    Message saved = messageRepository.save(message);
    MessageDto response = messageMapper.toDto(saved);

    messagingTemplate.convertAndSend(
      "/topic/conversations/" + incoming.conversationId(),
      response
    );
  }
}
