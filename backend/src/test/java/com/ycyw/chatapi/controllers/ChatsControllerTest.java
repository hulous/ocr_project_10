package com.ycyw.chatapi.controllers;

import com.ycyw.chatapi.dtos.IncomingMessageDto;
import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.services.ChatService;
import java.security.Principal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatsControllerTest {

  @Mock
  private ChatService chatService;

  @InjectMocks
  private ChatsController controller;

  @Test
  void sendPublishesSavedMessageWhenPrincipalIsAuthenticated() {
    Principal principal = () -> "john@example.com";
    IncomingMessageDto incoming = new IncomingMessageDto("demo", "hello world");
    MessageDto response = new MessageDto(null, incoming.conversationId(), "john@example.com", incoming.content(), Instant.now());

    when(chatService.sendMessage(principal.getName(), incoming)).thenReturn(response);

    controller.send(incoming, principal);

    verify(chatService).sendMessage(principal.getName(), incoming);
  }

  @Test
  void sendDoesNothingWhenPrincipalIsNull() {
    IncomingMessageDto incoming = new IncomingMessageDto("demo", "hello world");

    controller.send(incoming, null);

    verify(chatService, never()).sendMessage(anyString(), any(IncomingMessageDto.class));
  }

  @Test
  void sendDelegatesToServiceAndPropagatesException() {
    Principal principal = () -> "missing@example.com";
    IncomingMessageDto incoming = new IncomingMessageDto("demo", "hello world");

    when(chatService.sendMessage(principal.getName(), incoming)).thenThrow(new IllegalStateException("Authenticated user not found"));

    assertThrows(IllegalStateException.class, () -> controller.send(incoming, principal));
    verify(chatService).sendMessage(principal.getName(), incoming);
  }
}
