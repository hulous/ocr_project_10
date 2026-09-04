package com.ycyw.chatapi;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class BaseApplicationTests {

  @Test
  void constructorCanBeInstantiated() {
    new ChatApiApplication();
  }

  @Test
  void mainDelegatesToSpringApplicationRun() {
    String[] args = new String[] {"--spring.main.banner-mode=off"};

    try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
      ChatApiApplication.main(args);
      mocked.verify(() -> SpringApplication.run(ChatApiApplication.class, args));
    }
  }
}
