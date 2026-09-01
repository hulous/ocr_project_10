package com.ycyw.chatapi;

import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

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
