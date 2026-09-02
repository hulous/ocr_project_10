package com.ycyw.chatapi.configurations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigurationTest {

  @Mock private StompEndpointRegistry registry;

  @Mock private StompWebSocketEndpointRegistration registration;

  @Mock private SockJsServiceRegistration sockJsServiceRegistration;

  @Mock private MessageBrokerRegistry messageBrokerRegistry;

  @Mock private StompAuthChannelInterceptor stompAuthChannelInterceptor;

  @Test
  void registerStompEndpointsUsesConfiguredFrontendOrigin() {
    WebSocketConfiguration configuration = new WebSocketConfiguration(stompAuthChannelInterceptor);
    ReflectionTestUtils.setField(configuration, "frontendOrigin", "http://frontend.local:4250");
    when(registry.addEndpoint("/ws")).thenReturn(registration);
    when(registration.setAllowedOrigins("http://frontend.local:4250")).thenReturn(registration);
    when(registration.withSockJS()).thenReturn(sockJsServiceRegistration);

    configuration.registerStompEndpoints(registry);

    verify(registry).addEndpoint("/ws");
    verify(registration).setAllowedOrigins("http://frontend.local:4250");
    verify(registration).withSockJS();
  }

  @Test
  void configureMessageBrokerRegistersTopicAndAppDestinationPrefixes() {
    WebSocketConfiguration configuration = new WebSocketConfiguration(stompAuthChannelInterceptor);

    configuration.configureMessageBroker(messageBrokerRegistry);

    verify(messageBrokerRegistry).enableSimpleBroker("/topic");
    verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
  }
}
