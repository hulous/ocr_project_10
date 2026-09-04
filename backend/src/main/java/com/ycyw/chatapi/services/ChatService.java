package com.ycyw.chatapi.services;

import com.ycyw.chatapi.dtos.IncomingMessageDto;
import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.entities.Message;
import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.mappers.MessageMapper;
import com.ycyw.chatapi.repositories.MessageRepository;
import com.ycyw.chatapi.repositories.UserRepository;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatService(
      MessageRepository messageRepository,
      UserRepository userRepository,
      MessageMapper messageMapper,
      SimpMessagingTemplate messagingTemplate) {
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
    this.messageMapper = messageMapper;
    this.messagingTemplate = messagingTemplate;
  }

  public MessageDto sendMessage(String userEmail, IncomingMessageDto incoming) {
    User sender =
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

    Message message =
        new Message()
            .setConversationId(incoming.conversationId())
            .setSender(sender)
            .setContent(incoming.content())
            .setSentAt(java.time.Instant.now());

    Message saved = messageRepository.save(message);
    MessageDto response = messageMapper.toDto(saved);

    messagingTemplate.convertAndSend("/topic/conversations/" + incoming.conversationId(), response);

    return response;
  }

  public List<MessageDto> getConversationHistory(String conversationId) {
    return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
        .map(messageMapper::toDto)
        .toList();
  }
}
