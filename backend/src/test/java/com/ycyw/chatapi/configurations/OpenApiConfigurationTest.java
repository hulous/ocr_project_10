package com.ycyw.chatapi.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigurationTest {

  @Test
  void openApiBeanContainsBearerSecurityScheme() {
    OpenApiConfiguration configuration = new OpenApiConfiguration();

    OpenAPI openAPI = configuration.openAPI();

    assertNotNull(openAPI.getComponents());
    assertNotNull(openAPI.getComponents().getSecuritySchemes());
    assertNotNull(openAPI.getComponents().getSecuritySchemes().get("bearerAuth"));
    assertEquals("bearer", openAPI.getComponents().getSecuritySchemes().get("bearerAuth").getScheme());
  }
}
