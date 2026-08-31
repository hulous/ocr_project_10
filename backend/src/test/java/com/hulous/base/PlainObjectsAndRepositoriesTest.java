package com.hulous.base;

import com.hulous.base.dtos.LoginUserDto;
import com.hulous.base.dtos.RegisterUserDto;
import com.hulous.base.entities.User;
import com.hulous.base.repositories.UserRepository;
import com.hulous.base.responses.ApiMessageResponse;
import com.hulous.base.responses.LoginResponse;
import com.hulous.base.responses.UserResponse;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlainObjectsAndRepositoriesTest {

  @Test
  void dtoSettersGettersAndToStringWork() {
    LoginUserDto login = new LoginUserDto();
    RegisterUserDto register = new RegisterUserDto();

    assertSame(login, login.setEmail("a@b.com").setPassword("pwd"));
    assertSame(register, register.setEmail("a@b.com").setPassword("pwd").setName("Name"));

    assertEquals("a@b.com", login.getEmail());
    assertEquals("pwd", login.getPassword());
    assertEquals("a@b.com", register.getEmail());
    assertEquals("pwd", register.getPassword());
    assertEquals("Name", register.getName());
    assertTrue(login.toString().contains("email=a@b.com"));
    assertTrue(register.toString().contains("name=Name"));
    assertTrue(!login.toString().contains("pwd"));
    assertTrue(!register.toString().contains("pwd"));
  }

  @Test
  void entitySupportsUserDetailsAndHidesPasswordInToString() {
    Date createdAt = new Date();
    Date updatedAt = new Date();

    User user = new User()
      .setId(1)
      .setEmail("owner@example.com")
      .setPassword("pwd")
      .setName("Owner")
      .setCreatedAt(createdAt)
      .setUpdatedAt(updatedAt);

    assertEquals(Integer.valueOf(1), user.getId());
    assertEquals("owner@example.com", user.getUsername());
    assertEquals("Owner", user.getName());
    assertEquals(createdAt, user.getCreatedAt());
    assertEquals(updatedAt, user.getUpdatedAt());
    assertTrue(user.getAuthorities().isEmpty());
    assertTrue(user.isAccountNonExpired());
    assertTrue(user.isAccountNonLocked());
    assertTrue(user.isCredentialsNonExpired());
    assertTrue(user.isEnabled());
    assertTrue(!user.toString().contains("pwd"));
  }

  @Test
  void responseSettersAndGettersWork() {
    ApiMessageResponse apiMessageResponse = new ApiMessageResponse();
    LoginResponse loginResponse = new LoginResponse();
    UserResponse userResponse = new UserResponse();

    assertSame(apiMessageResponse, apiMessageResponse.setMessage("ok"));
    assertSame(loginResponse, loginResponse.setToken("token").setExpiresIn(3600L));
    assertSame(userResponse, userResponse.setId(1).setName("Alice").setEmail("a@b.com"));

    Date createdAt = new Date();
    Date updatedAt = new Date();
    assertSame(userResponse, userResponse.setCreatedAt(createdAt).setUpdatedAt(updatedAt));

    assertEquals("ok", apiMessageResponse.getMessage());
    assertEquals("token", loginResponse.getToken());
    assertEquals(3600L, loginResponse.getExpiresIn());
    assertEquals(Integer.valueOf(1), userResponse.getId());
    assertEquals("Alice", userResponse.getName());
    assertEquals("a@b.com", userResponse.getEmail());
    assertEquals(createdAt, userResponse.getCreatedAt());
    assertEquals(updatedAt, userResponse.getUpdatedAt());
    assertNotNull(apiMessageResponse.toString());
  }

  @Test
  void userRepositoryDeclaresTheExpectedContract() throws NoSuchMethodException {
    assertTrue(UserRepository.class.isAnnotationPresent(Repository.class));
    assertNotNull(UserRepository.class.getMethod("findByEmail", String.class));
    assertNotNull(UserRepository.class.getMethod("existsByEmail", String.class));
  }
}
