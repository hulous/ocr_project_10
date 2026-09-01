package com.ycyw.chatapi.configurations;

import com.ycyw.chatapi.services.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StompAuthChannelInterceptorTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private UserDetailsService userDetailsService;

  @InjectMocks
  private StompAuthChannelInterceptor interceptor;

  @Mock
  private MessageChannel messageChannel;

  @Test
  void preSendRejectsConnectWhenAuthorizationHeaderIsMissing() {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    AuthenticationCredentialsNotFoundException exception = assertThrows(
      AuthenticationCredentialsNotFoundException.class,
      () -> interceptor.preSend(message, messageChannel)
    );

    assertEquals("Token manquant", exception.getMessage());
    verify(jwtService, never()).extractUsername(null);
    verify(userDetailsService, never()).loadUserByUsername("any");
  }

  @Test
  void preSendRejectsConnectWhenTokenIsInvalidOrExpired() {
    String token = "jwt-token";
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    when(jwtService.extractUsername(token)).thenReturn("");

    AuthenticationCredentialsNotFoundException exception = assertThrows(
      AuthenticationCredentialsNotFoundException.class,
      () -> interceptor.preSend(message, messageChannel)
    );

    assertEquals("Token invalide ou expiré", exception.getMessage());
    verify(jwtService).extractUsername(token);
    verify(userDetailsService, never()).loadUserByUsername("any");
  }

  @Test
  void preSendAcceptsConnectWhenAuthorizationHeaderIsLowercase() {
    String token = "jwt-token";
    String email = "john@example.com";
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("authorization", "Bearer " + token);
    Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    UserDetails userDetails = User.withUsername(email).password("pwd").authorities(List.of()).build();
    when(jwtService.extractUsername(token)).thenReturn(email);
    when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
    when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

    Message<?> result = interceptor.preSend(message, messageChannel);

    assertNotNull(result);
    verify(jwtService).extractUsername(token);
    verify(userDetailsService).loadUserByUsername(email);
    verify(jwtService).isTokenValid(token, userDetails);
  }

  @Test
  void preSendRejectsConnectWhenTokenValidationFails() {
    String token = "jwt-token";
    String email = "john@example.com";
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    UserDetails userDetails = User.withUsername(email).password("pwd").authorities(List.of()).build();
    when(jwtService.extractUsername(token)).thenReturn(email);
    when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
    when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

    AuthenticationCredentialsNotFoundException exception = assertThrows(
      AuthenticationCredentialsNotFoundException.class,
      () -> interceptor.preSend(message, messageChannel)
    );

    assertEquals("Token invalide ou expiré", exception.getMessage());
    verify(jwtService).extractUsername(token);
    verify(userDetailsService).loadUserByUsername(email);
  }

  @Test
  void preSendAcceptsConnectWhenTokenIsValid() {
    String token = "jwt-token";
    String email = "john@example.com";
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer " + token);
    Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    UserDetails userDetails = User.withUsername(email).password("pwd").authorities(List.of()).build();
    when(jwtService.extractUsername(token)).thenReturn(email);
    when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
    when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

    Message<?> result = interceptor.preSend(message, messageChannel);

    assertNotNull(result);
    verify(jwtService).extractUsername(token);
    verify(userDetailsService).loadUserByUsername(email);
    verify(jwtService).isTokenValid(token, userDetails);
  }
}
