package com.ycyw.chatapi;

import com.ycyw.chatapi.dtos.MessageDto;
import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.repositories.MessageRepository;
import com.ycyw.chatapi.repositories.UserRepository;
import com.ycyw.chatapi.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebSocketIntegrationTest.SubscriptionEventConfiguration.class)
class WebSocketIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MessageRepository messageRepository;

  @AfterEach
  void cleanup() {
    messageRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void websocketMessagePublishedByOneClientIsReceivedBySubscriber() throws Exception {
    User sender = new User()
      .setName("John Doe")
      .setEmail("john@example.com")
      .setPassword("password");
    userRepository.save(sender);

    String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
      .withUsername(sender.getEmail())
      .password("password")
      .authorities(List.of())
      .build());

    WebSocketStompClient stompClient = new WebSocketStompClient(
      new SockJsClient(List.of(transport()))
    );
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    stompClient.setMessageConverter(new MappingJackson2MessageConverter(objectMapper));
    stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

    String url = String.format("ws://localhost:%d/ws", port);
    WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
    webSocketHttpHeaders.setOrigin("http://localhost");

    StompHeaders connectHeaders = new StompHeaders();
    connectHeaders.add("Authorization", "Bearer " + token);

    CompletableFuture<MessageDto> receivedMessage = new CompletableFuture<>();
    StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
      @Override
      public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        receivedMessage.completeExceptionally(exception);
      }

      @Override
      public void handleTransportError(StompSession session, Throwable exception) {
        receivedMessage.completeExceptionally(exception);
      }
    };

    StompSession subscriberSession = connect(stompClient, url, webSocketHttpHeaders, connectHeaders, sessionHandler);
    subscriberSession.subscribe("/topic/conversations/demo", new org.springframework.messaging.simp.stomp.StompFrameHandler() {
      @Override
      public Type getPayloadType(StompHeaders headers) {
        return MessageDto.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
        receivedMessage.complete((MessageDto) payload);
      }
    });

    assertEquals(true, SubscriptionEventConfiguration.SUBSCRIPTION_RECEIVED.await(10, TimeUnit.SECONDS));
    StompSession senderSession = connect(stompClient, url, webSocketHttpHeaders, connectHeaders, sessionHandler);

    Map<String, String> outgoing = Map.of(
      "conversationId", "demo",
      "content", "Hello from client"
    );

    StompHeaders outgoingHeaders = new StompHeaders();
    outgoingHeaders.setDestination("/app/chat.send");
    outgoingHeaders.setContentType(MediaType.APPLICATION_JSON);
    senderSession.send(outgoingHeaders, outgoing);

    MessageDto result = receivedMessage.get(10, TimeUnit.SECONDS);

    assertNotNull(result);
    assertEquals("demo", result.conversationId());
    assertEquals("john@example.com", result.senderEmail());
    assertEquals("Hello from client", result.content());
    assertNotNull(result.id());
    assertNotNull(result.sentAt());

    senderSession.disconnect();
    subscriberSession.disconnect();
  }

  private Transport transport() {
    return new WebSocketTransport(new StandardWebSocketClient());
  }

  private StompSession connect(WebSocketStompClient stompClient, String url, WebSocketHttpHeaders httpHeaders, StompHeaders connectHeaders, StompSessionHandlerAdapter sessionHandler) throws Exception {
    CompletableFuture<StompSession> future = stompClient.connectAsync(url, httpHeaders, connectHeaders, sessionHandler);
    return future.get(10, TimeUnit.SECONDS);
  }

  @TestConfiguration
  static class SubscriptionEventConfiguration {
    private static final CountDownLatch SUBSCRIPTION_RECEIVED = new CountDownLatch(1);

    @Bean
    org.springframework.context.ApplicationListener<SessionSubscribeEvent> subscriptionListener() {
      return event -> {
        String destination = org.springframework.messaging.simp.stomp.StompHeaderAccessor
          .wrap(event.getMessage())
          .getDestination();
        if ("/topic/conversations/demo".equals(destination)) {
          SUBSCRIPTION_RECEIVED.countDown();
        }
      };
    }
  }
}
