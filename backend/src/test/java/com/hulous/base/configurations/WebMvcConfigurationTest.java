package com.hulous.base.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebMvcConfigurationTest {

  @Test
  void addResourceHandlersRegistersUploadsMapping() {
    WebMvcConfiguration configuration = new WebMvcConfiguration();

    ResourceHandlerRegistry registry = new ResourceHandlerRegistry(
      new StaticApplicationContext(),
      new MockServletContext()
    );
    configuration.addResourceHandlers(registry);

    assertTrue(registry.hasMappingForPattern("/uploads/**"));
  }
}
