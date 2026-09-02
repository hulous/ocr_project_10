package com.ycyw.chatapi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.services.ChatService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationsControllerTest {

  @Mock private ChatService chatService;

  @InjectMocks private ConversationsController controller;

  @Test
  void getHistoryReturnsMappedMessagesInOrder() {
    String conversationId = "demo";
    MessageDto dto1 = new MessageDto(null, conversationId, "john@example.com", "Hello", null);
    MessageDto dto2 = new MessageDto(null, conversationId, "john@example.com", "Bye", null);

    when(chatService.getConversationHistory(conversationId)).thenReturn(List.of(dto1, dto2));

    List<MessageDto> response = controller.getHistory(conversationId);

    assertEquals(List.of(dto1, dto2), response);
    verify(chatService).getConversationHistory(conversationId);
  }

  @Test
  void getHistoryReturnsEmptyListWhenNoMessagesExist() {
    String conversationId = "empty";

    when(chatService.getConversationHistory(conversationId)).thenReturn(List.of());

    List<MessageDto> response = controller.getHistory(conversationId);

    assertTrue(response.isEmpty());
    verify(chatService).getConversationHistory(conversationId);
  }
}
