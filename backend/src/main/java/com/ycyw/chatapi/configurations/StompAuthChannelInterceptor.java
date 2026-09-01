package com.ycyw.chatapi.configurations;

import com.ycyw.chatapi.services.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public StompAuthChannelInterceptor(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null || !accessor.isMutable()) {
      accessor = StompHeaderAccessor.wrap(message);
    }

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = extractBearerToken(accessor);

      if (!StringUtils.hasText(token)) {
        throw new AuthenticationCredentialsNotFoundException("Token manquant");
      }

      String email = jwtService.extractUsername(token);

      if (!StringUtils.hasText(email)) {
        throw new AuthenticationCredentialsNotFoundException("Token invalide ou expiré");
      }

      UserDetails userDetails = userDetailsService.loadUserByUsername(email);

      if (!jwtService.isTokenValid(token, userDetails)) {
        throw new AuthenticationCredentialsNotFoundException("Token invalide ou expiré");
      }

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
      );

      accessor.setUser(authentication);
    }

    return message;
  }

  private String extractBearerToken(StompHeaderAccessor accessor) {
    List<String> authHeaders = accessor.getNativeHeader(AUTHORIZATION_HEADER);

    if (authHeaders == null || authHeaders.isEmpty()) {
      authHeaders = accessor.getNativeHeader(AUTHORIZATION_HEADER.toLowerCase());
    }

    if (authHeaders == null || authHeaders.isEmpty()) {
      return null;
    }

    String rawHeader = authHeaders.get(0);
    if (!StringUtils.hasText(rawHeader) || !rawHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }

    return rawHeader.substring(BEARER_PREFIX.length()).trim();
  }
}
