package com.ycyw.chatapi.services;

import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.exceptions.ApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserServiceTest {

  private final CurrentUserService service = new CurrentUserService();

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getCurrentUserReturnsAuthenticatedUser() {
    User user = new User().setId(1).setEmail("john@example.com");
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));

    User result = service.getCurrentUser();

    assertEquals(user, result);
    assertEquals(1, result.getId());
    assertEquals("john@example.com", result.getEmail());
  }

  @Test
  void getCurrentUserThrowsWhenNoAuthenticationExists() {
    ApiException exception = assertThrows(ApiException.class, () -> service.getCurrentUser());

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("No authenticated user found", exception.getMessage());
  }

  @Test
  void getCurrentUserThrowsWhenPrincipalIsNotUser() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("string-principal", null));

    ApiException exception = assertThrows(ApiException.class, () -> service.getCurrentUser());

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    assertEquals("Unauthorized request", exception.getMessage());
  }

  @Test
  void getCurrentUserOrNullReturnsUserWhenAuthenticated() {
    User user = new User().setId(2).setEmail("alice@example.com");
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));

    User result = service.getCurrentUserOrNull();

    assertEquals(user, result);
  }

  @Test
  void getCurrentUserOrNullReturnsNullWhenNotAuthenticated() {
    User result = service.getCurrentUserOrNull();

    assertNull(result);
  }

  @Test
  void getCurrentUserOrNullReturnsNullWhenPrincipalIsNotUser() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("not-a-user", null));

    User result = service.getCurrentUserOrNull();

    assertNull(result);
  }
}
