package com.ycyw.chatapi.services;

import com.ycyw.chatapi.dtos.IncomingMessageDto;
import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.entities.Message;
import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.mappers.MessageMapper;
import com.ycyw.chatapi.repositories.MessageRepository;
import com.ycyw.chatapi.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock
  private MessageRepository messageRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MessageMapper messageMapper;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private ChatService chatService;

  @Test
  void sendMessageSavesAndPublishesMessage() {
    String userEmail = "john@example.com";
    IncomingMessageDto incoming = new IncomingMessageDto("demo", "hello world");
    User sender = new User().setId(1).setEmail(userEmail).setName("John").setPassword("secret");
    Message savedMessage = new Message()
      .setId(UUID.randomUUID())
      .setConversationId(incoming.conversationId())
      .setSender(sender)
      .setContent(incoming.content())
      .setSentAt(Instant.parse("2025-01-01T12:00:00Z"));
    MessageDto expectedDto = new MessageDto(savedMessage.getId(), "demo", userEmail, "hello world", savedMessage.getSentAt());

    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(sender));
    when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
    when(messageMapper.toDto(savedMessage)).thenReturn(expectedDto);

    MessageDto result = chatService.sendMessage(userEmail, incoming);

    assertEquals(expectedDto, result);
    verify(messagingTemplate).convertAndSend("/topic/conversations/demo", expectedDto);
  }

  @Test
  void sendMessageThrowsWhenAuthenticatedUserIsNotFound() {
    String userEmail = "missing@example.com";
    IncomingMessageDto incoming = new IncomingMessageDto("demo", "hello world");

    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

    IllegalStateException exception = assertThrows(IllegalStateException.class,
      () -> chatService.sendMessage(userEmail, incoming));

    assertEquals("Authenticated user not found", exception.getMessage());
  }

  @Test
  void getConversationHistoryReturnsOrderedMessageDtos() {
    String conversationId = "demo";
    User sender = new User().setId(1).setEmail("john@example.com");
    Message first = new Message()
      .setId(UUID.randomUUID())
      .setConversationId(conversationId)
      .setSender(sender)
      .setContent("first")
      .setSentAt(Instant.parse("2025-01-01T10:00:00Z"));
    Message second = new Message()
      .setId(UUID.randomUUID())
      .setConversationId(conversationId)
      .setSender(sender)
      .setContent("second")
      .setSentAt(Instant.parse("2025-01-01T11:00:00Z"));
    MessageDto firstDto = new MessageDto(first.getId(), conversationId, sender.getEmail(), "first", first.getSentAt());
    MessageDto secondDto = new MessageDto(second.getId(), conversationId, sender.getEmail(), "second", second.getSentAt());

    when(messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)).thenReturn(List.of(first, second));
    when(messageMapper.toDto(first)).thenReturn(firstDto);
    when(messageMapper.toDto(second)).thenReturn(secondDto);

    List<MessageDto> result = chatService.getConversationHistory(conversationId);

    assertEquals(List.of(firstDto, secondDto), result);
  }
}
