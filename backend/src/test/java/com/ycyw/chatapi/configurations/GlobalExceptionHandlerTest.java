package com.ycyw.chatapi.configurations;

import com.ycyw.chatapi.exceptions.ApiException;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleValidationExceptionConcatenatesFieldMessages() throws Exception {
    Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleValidationTarget", String.class);
    MethodParameter parameter = new MethodParameter(method, 0);
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "payload");
    bindingResult.addError(new FieldError("payload", "email", "Email is required"));
    bindingResult.addError(new FieldError("payload", "password", "Password is required"));

    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

    var response = handler.handleValidationException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Email is required, Password is required", response.getBody().getMessage());
  }

  @Test
  void handleBindExceptionReturnsDefaultMessageWhenBlank() {
    BindException exception = new BindException(new Object(), "payload");
    exception.addError(new FieldError("payload", "name", ""));

    var response = handler.handleValidationException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid request payload", response.getBody().getMessage());
  }

  @Test
  void handleBindExceptionConcatenatesNonBlankFieldMessages() {
    BindException exception = new BindException(new Object(), "payload");
    exception.addError(new FieldError("payload", "name", "Name is required"));
    exception.addError(new FieldError("payload", "surface", "Surface must be positive"));

    var response = handler.handleValidationException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Name is required, Surface must be positive", response.getBody().getMessage());
  }

  @Test
  void handleApiExceptionUsesEmbeddedStatus() {
    ApiException exception = new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized request");

    var response = handler.handleApiException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Unauthorized request", response.getBody().getMessage());
  }

  @Test
  void handleAuthenticationExceptionReturnsUnauthorized() {
    var response = handler.handleAuthenticationException(new RuntimeException("boom"));

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid email or password", response.getBody().getMessage());
  }

  @Test
  void handleClassCastExceptionReturnsUnauthorized() {
    var response = handler.handleClassCastException(new ClassCastException("boom"));

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Unauthorized request", response.getBody().getMessage());
  }

  @Test
  void handleUnexpectedExceptionReturnsGenericMessage() {
    var response = handler.handleUnexpectedException(new RuntimeException("boom"));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Internal server error", response.getBody().getMessage());
  }

  @SuppressWarnings("unused")
  private void sampleValidationTarget(String payload) {
    // Test fixture for MethodParameter creation.
  }
}
