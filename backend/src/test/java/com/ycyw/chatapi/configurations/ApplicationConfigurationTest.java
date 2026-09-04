package com.ycyw.chatapi.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.ycyw.chatapi.entities.User;
import com.ycyw.chatapi.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigurationTest {

  @Mock private UserRepository userRepository;

  @Mock private AuthenticationConfiguration authenticationConfiguration;

  @Mock private AuthenticationManager authenticationManager;

  @Test
  void userDetailsServiceLoadsUserByEmail() {
    ApplicationConfiguration configuration = new ApplicationConfiguration(userRepository);
    User user = new User().setEmail("john@example.com");
    when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

    UserDetailsService service = configuration.userDetailsService();

    assertEquals(user, service.loadUserByUsername("john@example.com"));
  }

  @Test
  void userDetailsServiceThrowsWhenUserMissing() {
    ApplicationConfiguration configuration = new ApplicationConfiguration(userRepository);
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    UserDetailsService service = configuration.userDetailsService();

    UsernameNotFoundException exception =
        assertThrows(
            UsernameNotFoundException.class,
            () -> service.loadUserByUsername("missing@example.com"));
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void passwordEncoderBeanIsAvailable() {
    ApplicationConfiguration configuration = new ApplicationConfiguration(userRepository);

    BCryptPasswordEncoder encoder = configuration.passwordEncoder();

    assertNotNull(encoder);
    assertNotNull(encoder.encode("pwd"));
  }

  @Test
  void authenticationManagerDelegatesToConfiguration() throws Exception {
    ApplicationConfiguration configuration = new ApplicationConfiguration(userRepository);
    when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

    AuthenticationManager result = configuration.authenticationManager(authenticationConfiguration);

    assertEquals(authenticationManager, result);
  }

  @Test
  void authenticationProviderReturnsDaoAuthenticationProvider() {
    ApplicationConfiguration configuration = new ApplicationConfiguration(userRepository);

    AuthenticationProvider provider = configuration.authenticationProvider();

    assertInstanceOf(DaoAuthenticationProvider.class, provider);
  }
}
