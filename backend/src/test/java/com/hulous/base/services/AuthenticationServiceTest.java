package com.hulous.base.services;

import com.hulous.base.dtos.LoginUserDto;
import com.hulous.base.dtos.RegisterUserDto;
import com.hulous.base.entities.User;
import com.hulous.base.repositories.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthenticationService service;

  @Test
  void registrateThrowsWhenEmailAlreadyExists() {
    RegisterUserDto dto = new RegisterUserDto().setEmail("john@example.com");
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.registrate(dto));

    assertEquals("A user with this email already exists", exception.getMessage());
  }

  @Test
  void registrateSavesEncodedPassword() {
    RegisterUserDto dto = new RegisterUserDto().setName("John").setEmail("john@example.com").setPassword("raw");
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(passwordEncoder.encode("raw")).thenReturn("encoded");

    User saved = new User().setId(1).setEmail("john@example.com").setName("John").setPassword("encoded");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    User result = service.registrate(dto);

    assertEquals(1, result.getId());
    assertEquals("encoded", result.getPassword());
  }

  @Test
  void authenticateReturnsUserWhenCredentialsAreValid() {
    LoginUserDto dto = new LoginUserDto().setEmail("john@example.com").setPassword("pwd");
    User user = new User().setEmail("john@example.com");

    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

    User result = service.authenticate(dto);

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    assertEquals("john@example.com", result.getEmail());
  }

  @Test
  void authenticateThrowsWhenUserCannotBeFoundAfterAuth() {
    LoginUserDto dto = new LoginUserDto().setEmail("john@example.com").setPassword("pwd");
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.authenticate(dto));

    assertEquals("Invalid credentials", exception.getMessage());
  }
}
