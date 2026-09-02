package com.ycyw.chatapi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.exceptions.ApiException;
import com.ycyw.chatapi.responses.UserResponse;
import com.ycyw.chatapi.services.UserService;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

  @Mock private UserService userService;

  @InjectMocks private UsersController controller;

  @Test
  void showReturnsUserWhenFound() {
    User user =
        new User()
            .setId(1)
            .setName("John")
            .setEmail("john@example.com")
            .setCreatedAt(new Date())
            .setUpdatedAt(new Date());
    UserResponse userResponse =
        new UserResponse()
            .setId(1)
            .setName("John")
            .setEmail("john@example.com")
            .setCreatedAt(user.getCreatedAt())
            .setUpdatedAt(user.getUpdatedAt());
    when(userService.show(1)).thenReturn(userResponse);

    ResponseEntity<UserResponse> response = controller.show(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    UserResponse body = response.getBody();
    assertEquals(userResponse.getId(), body.getId());
    assertEquals(userResponse.getEmail(), body.getEmail());
  }

  @Test
  void showReturnsNotFoundWhenNotFound() {
    when(userService.show(10)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "User not found"));

    ApiException exception = assertThrows(ApiException.class, () -> controller.show(10));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void showReturnsInternalServerErrorOnUnexpectedException() {
    when(userService.show(10)).thenThrow(new RuntimeException("boom"));

    assertThrows(RuntimeException.class, () -> controller.show(10));
  }
}
